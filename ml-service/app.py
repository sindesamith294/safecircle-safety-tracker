import math
import numpy as np
from datetime import datetime
from flask import Flask, request, jsonify
from sklearn.ensemble import IsolationForest

app = Flask(__name__)

# Haversine formula for distance between GPS coordinates in kilometers
def haversine_km(lat1, lon1, lat2, lon2):
    R = 6371.0  # Earth radius in kilometers
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = (math.sin(dlat / 2) ** 2 +
         math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) *
         math.sin(dlon / 2) ** 2)
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return R * c

# Initialize and train Isolation Forest on normal movement patterns
# Features: [speed_kmh, elapsed_sec, accuracy_m]
def train_baseline_model():
    rng = np.random.RandomState(42)
    # Generate 1000 normal urban commute data points
    # speed between 0 and 70 km/h, elapsed time 10-180 seconds, accuracy 3-25m
    speeds = rng.normal(loc=25.0, scale=12.0, size=(1000, 1))
    speeds = np.clip(speeds, 0.0, 75.0)

    elapsed = rng.exponential(scale=30.0, size=(1000, 1)) + 5.0
    accuracy = rng.normal(loc=8.0, scale=3.0, size=(1000, 1))
    accuracy = np.clip(accuracy, 2.0, 30.0)

    X_train = np.hstack([speeds, elapsed, accuracy])

    model = IsolationForest(contamination=0.03, random_state=42)
    model.fit(X_train)
    return model

model = train_baseline_model()

@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        "status": "UP",
        "service": "SafeCircle ML Anomaly Detection Service",
        "model": "IsolationForest"
    })

@app.route('/predict-anomaly', methods=['POST'])
def predict_anomaly():
    """
    Evaluates GPS jump and personal movement patterns.
    Input payload:
    {
      "previous": {"latitude": 37.7749, "longitude": -122.4194, "recordedAt": "2026-10-21T08:00:00Z"},
      "current": {"latitude": 37.8024, "longitude": -122.4058, "accuracyM": 5.0, "recordedAt": "2026-10-21T08:00:10Z"}
    }
    """
    payload = request.get_json() or {}
    prev = payload.get("previous")
    curr = payload.get("current")

    if not curr:
        return jsonify({"error": "Missing current location payload"}), 400

    # If this is the user's first location point, no anomaly can be calculated
    if not prev or "latitude" not in prev or "longitude" not in prev:
        return jsonify({
            "is_anomaly": False,
            "anomaly_score": 0.0,
            "speed_kmh": 0.0,
            "reason": "First GPS fix: establishing baseline position."
        })

    # Parse timestamps
    try:
        t_prev = datetime.fromisoformat(prev["recordedAt"].replace("Z", "+00:00"))
        t_curr = datetime.fromisoformat(curr["recordedAt"].replace("Z", "+00:00"))
        elapsed_sec = max((t_curr - t_prev).total_seconds(), 0.1)
    except Exception:
        elapsed_sec = 10.0

    # Calculate distance and speed
    distance_km = haversine_km(
        float(prev["latitude"]), float(prev["longitude"]),
        float(curr["latitude"]), float(curr["longitude"])
    )
    speed_kmh = (distance_km / elapsed_sec) * 3600.0
    accuracy_m = float(curr.get("accuracyM", 10.0))

    reasons = []
    is_anomaly = False

    # Check A: Physical GPS Implausibility Rule (>140 km/h sudden jump in city)
    if speed_kmh > 140.0:
        is_anomaly = True
        reasons.append(f"Physically implausible GPS jump: estimated speed {speed_kmh:.1f} km/h exceeds 140 km/h.")

    # Check B: Isolation Forest Model Anomaly Score
    feature_vector = np.array([[speed_kmh, elapsed_sec, accuracy_m]])
    model_pred = model.predict(feature_vector)[0] # -1 for anomaly, 1 for normal
    raw_score = float(model.score_samples(feature_vector)[0])
    # Normalize score roughly into [0, 1] range
    anomaly_score = round(max(0.0, min(1.0, -raw_score)), 3)

    if model_pred == -1 and not is_anomaly:
        is_anomaly = True
        reasons.append(f"Movement anomaly detected by Isolation Forest model (score: {anomaly_score}).")

    if not is_anomaly:
        reason_text = "Movement within expected parameters."
    else:
        reason_text = " | ".join(reasons)

    return jsonify({
        "is_anomaly": is_anomaly,
        "anomaly_score": anomaly_score,
        "speed_kmh": round(speed_kmh, 2),
        "distance_km": round(distance_km, 3),
        "elapsed_seconds": round(elapsed_sec, 1),
        "accuracy_m": accuracy_m,
        "reason": reason_text
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)
