<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${sessionScope.user != null}">
    <div class="dropdown">
        <button class="btn btn-primary" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            <i class="fa fa-bell"></i>
            <span class="badge badge-primary">${sessionScope.user.pendingFriendRequests.size()}</span>
        </button>
        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton">

            <a class="dropdown-item">

                <button type="button" class="btn btn-light" data-toggle="modal" data-target="#friendRequestsModal">
                    <p class="ui red circular label">
                        ${sessionScope.user.pendingFriendRequests.size()}
                    </p> Friend Requests
                </button>

                <!-- Modal -->
                <div class="modal fade" id="friendRequestsModal" tabindex="-1" role="dialog" aria-labelledby="friendRequestsModalLabel" aria-hidden="true">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="friendRequestsModalLabel">New Friend Requests</h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">
                                <jsp:include page="profile/friend-request-table.jsp"/>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            </a>
            <div class="dropdown-divider"></div>
            <a class="dropdown-item">
                <a class="mini ui button negative" href="logout">
                    <i class="fas fa-sign-out-alt"></i> Sign out
                </a>
            </a>
        </div>

    </div>
</c:if>
