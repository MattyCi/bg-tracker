package org.bgtrack.auth;

import org.bgtrack.utils.BGTConstants;

public class JsonAction extends ShiroBaseAction {
	private static final long serialVersionUID = 1L;
	
	private JsonError jsonErrorObject;

	@Override
	public void validate() {
		
		if (isAuthenticationRequired() && !isAuthenticated()) {
			
			this.setJsonErrorObject(new JsonError(2, BGTConstants.LOG_IN_PROMPT));
			this.addActionError(BGTConstants.LOG_IN_PROMPT);
			
		}
		
		if (isCsrfProtected() && isCsrfDetected()) {
			
			this.setJsonErrorObject(new JsonError(2, "CSRF request detected. Please log in again."));
			addActionError("CSRF request detected. Please log in again.");
			
			LOG.info("CSRF attempt detected for user " + shiroUser.getPrincipal() + " requesting action: " + this.getClass().getName());
			
		}
		
	}
	
	public JsonError getJsonErrorObject() {
		return jsonErrorObject;
	}

	public void setJsonErrorObject(JsonError jsonErrorObject) {
		this.jsonErrorObject = jsonErrorObject;
	}
	
	public class JsonError {
		
		int statusCode;
		String errorMessage;

		public JsonError(int statusCode, String jsonErrorMessage) {
			this.setStatusCode(statusCode);
			this.setErrorMessage(jsonErrorMessage);
		}
		
		public int getStatusCode() {
			return statusCode;
		}
		
		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}
		
		public String getErrorMessage() {
			return errorMessage;
		}
		
		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
		
	}
	
}
