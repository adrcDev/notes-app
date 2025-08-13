
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
    let inputIsValidUserName=isValidUserName(trimmedValue);
    if(inputIsValidUserName) {
      return true;
    }
    else {
      let errorMessage="Invalid username!";
      return errorMessage;
    }
  }
}

function isValidUserName(trimmedValue) {
  let userNameRegex=/^[a-zA-Z0-9][a-zA-Z0-9._-]{1,28}[a-zA-Z0-9]$/;
  let regexCheckResult=userNameRegex.test(trimmedValue);
  
  if(!regexCheckResult)
    return false;

  if(isStringContainsConsecutiveSpecialChar(trimmedValue))
    return false;
  else
    return true;
}

 function isStringContainsConsecutiveSpecialChar(trimmedValue) {
  if(trimmedValue.includes("..") || trimmedValue.includes("__") || trimmedValue.includes("--") || trimmedValue.includes("._") || trimmedValue.includes(".-") || trimmedValue.includes("_-") ||
  trimmedValue.includes("_.") || trimmedValue.includes("-_") ||
  trimmedValue.includes("-."))
    return true;
  else
    return false;
}



