package com.antgroup.openspgapp.server.api.facade.dto.reasoner.session;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/session/UpdateSessionRequest.class */
public class UpdateSessionRequest extends CreateSessionRequest {
  private static final long serialVersionUID = 2721946536933394886L;
  private Long id;

  public UpdateSessionRequest(Long projectId, Long userId, String name, String description) {
    super(projectId, userId, name, description);
  }

  public UpdateSessionRequest(
      Long id, Long projectId, Long userId, String name, String description) {
    super(projectId, userId, name, description);
    this.id = id;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
