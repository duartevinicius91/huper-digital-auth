package huper.digital.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResponseDto {
  private String message;
  private Boolean success;

  public static class ErrorResponse extends ResponseDto {
    private String error;

    public ErrorResponse(String message, String error) {
      super(message, false);
      this.error = error;
    }
  }

  public static class SuccessResponse extends ResponseDto {
    public SuccessResponse(String message) {
      super(message, true);
    }
  }
}
