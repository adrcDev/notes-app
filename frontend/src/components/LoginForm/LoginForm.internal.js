
export function validateUserNameOrEmailTextField(value) {
  let trimmedValue=value.trim();
  let isUserTypingInEmail=trimmedValue.includes("@");
  if(isUserTypingInEmail) {
    let emailRegex=/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    let inputIsValidEmail=emailRegex.test(trimmedValue);
    if(inputIsValidEmail) {
      return true;
    }
    else {
      let errorMessage="Invalid email!";
      return errorMessage;
    }
  }
  else {
    return true;
  }
}



