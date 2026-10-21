package com.safecircle.controller;

import com.safecircle.dto.ApiResponse;
import com.safecircle.dto.SharingPermissionResponse;
import com.safecircle.dto.SharingRequestDto;
import com.safecircle.security.UserPrincipal;
import com.safecircle.service.SharingPermissionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class SharingPermissionController {

    private final SharingPermissionService permissionService;

    public SharingPermissionController(SharingPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * POST /api/sharing-requests
     * Current user (viewer) requests access to another user's location via their phone number.
     */
    @PostMapping("/api/sharing-requests")
    public ResponseEntity<ApiResponse<SharingPermissionResponse>> requestSharing(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SharingRequestDto dto) {
        SharingPermissionResponse response = permissionService.requestSharing(principal.getId(), dto);
        return ResponseEntity.ok(ApiResponse.ok("Location sharing request sent successfully", response));
    }

    /**
     * GET /api/sharing-requests/pending
     * List incoming sharing requests awaiting the current user's approval.
     */
    @GetMapping("/api/sharing-requests/pending")
    public ResponseEntity<ApiResponse<List<SharingPermissionResponse>>> getPendingRequests(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<SharingPermissionResponse> pending = permissionService.getIncomingPendingRequests(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Pending requests retrieved", pending));
    }

    /**
     * PATCH /api/sharing-requests/{id}/approve
     * Location owner approves the request, granting live GPS visibility.
     */
    @PatchMapping("/api/sharing-requests/{id}/approve")
    public ResponseEntity<ApiResponse<SharingPermissionResponse>> approveRequest(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long permissionId) {
        SharingPermissionResponse response = permissionService.approveRequest(principal.getId(), permissionId);
        return ResponseEntity.ok(ApiResponse.ok("Sharing request approved. Location sharing is now ACTIVE.", response));
    }

    /**
     * PATCH /api/sharing-requests/{id}/reject
     * Location owner rejects the request.
     */
    @PatchMapping("/api/sharing-requests/{id}/reject")
    public ResponseEntity<ApiResponse<SharingPermissionResponse>> rejectRequest(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long permissionId) {
        SharingPermissionResponse response = permissionService.rejectRequest(principal.getId(), permissionId);
        return ResponseEntity.ok(ApiResponse.ok("Sharing request rejected.", response));
    }

    /**
     * DELETE /api/sharing-permissions/{id}
     * Revokes sharing permission immediately. Access to subsequent updates stops instantly.
     */
    @DeleteMapping("/api/sharing-permissions/{id}")
    public ResponseEntity<ApiResponse<SharingPermissionResponse>> revokePermission(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long permissionId) {
        SharingPermissionResponse response = permissionService.revokePermission(principal.getId(), permissionId);
        return ResponseEntity.ok(ApiResponse.ok("Sharing permission revoked. Access to location updates is now stopped.", response));
    }

    /**
     * GET /api/sharing-permissions
     * List active contacts whose location the current user is permitted to view.
     */
    @GetMapping("/api/sharing-permissions")
    public ResponseEntity<ApiResponse<List<SharingPermissionResponse>>> getAuthorizedContacts(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<SharingPermissionResponse> approved = permissionService.getApprovedContactsToView(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Authorized contacts retrieved", approved));
    }

    /**
     * GET /api/sharing-permissions/viewers
     * List users who are currently permitted to view the current user's location.
     */
    @GetMapping("/api/sharing-permissions/viewers")
    public ResponseEntity<ApiResponse<List<SharingPermissionResponse>>> getActiveViewers(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<SharingPermissionResponse> viewers = permissionService.getActiveViewers(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Active viewers retrieved", viewers));
    }

    /**
     * GET /api/sharing-permissions/all
     * List all permissions involving current user (as owner or viewer).
     */
    @GetMapping("/api/sharing-permissions/all")
    public ResponseEntity<ApiResponse<List<SharingPermissionResponse>>> getAllPermissions(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<SharingPermissionResponse> all = permissionService.getAllPermissionsForUser(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("All permissions retrieved", all));
    }
}
