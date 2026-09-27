// Our Spring Boot backend returns errors like { message: "...", status: 404 }
// for handled cases, but network failures or unexpected errors look
// different. This normalizes all of them into one readable string.
export function getErrorMessage(error) {
  if (error.response?.data?.message) {
    return error.response.data.message;
  }
  if (error.response?.status === 403) {
    return "You don't have permission to do that.";
  }
  if (error.response?.status === 401) {
    return "Your session has expired. Please log in again.";
  }
  if (error.request) {
    return "Could not reach the server. Is the backend running?";
  }
  return error.message || "Something went wrong.";
}
