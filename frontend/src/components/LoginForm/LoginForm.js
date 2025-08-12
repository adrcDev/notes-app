import React from "react";
import ReactDOM from "react-dom";
import * as loginFormStylesObj from "./LoginForm.css";
import {useForm} from "react-hook-form";
import { Link } from "react-router";

export default function LoginForm() {
  let {register,handleSubmit,getValues,formState : {errors}}=useForm();
  let [isShowPasswordCheckboxChecked,setIsShowPasswordCheckboxChecked]=React.useState(false);
  let passwordTextFieldRef=React.useRef(null);
  let loginProcessingModalDialogRef=React.useRef(null);
  let invalidLoginDialogRef=React.useRef(null);
  let networkErrorDialogRef=React.useRef(null);
  let serverErrorDialogRef=React.useRef(null);
  

  let {ref:reactHookFormsInternalRef,...registerWithReactHookFormObj}=register("password",{
    required: { value:true,message: "Please enter the password"}
  });
  
  let classNamesForUserNameOrEmailTextField=loginFormStylesObj.textField;
  if(errors.userNameOrEmailTextField!==undefined) {
    classNamesForUserNameOrEmailTextField=`${classNamesForUserNameOrEmailTextField} ${loginFormStylesObj.errorStateForTextField}`
  }

  let typeForPasswordTextField;
  if(isShowPasswordCheckboxChecked) {
    typeForPasswordTextField="text";
  } else {
    typeForPasswordTextField="password";
  }

  let classNamesForPasswordTextField=loginFormStylesObj.textField;
  if(errors.passwordTextField!==undefined) {
    classNamesForPasswordTextField=`${classNamesForPasswordTextField} ${loginFormStylesObj.errorStateForTextField}`
  }

  function handleChangeForShowPasswordCheckbox(e) {
    let showPasswordCheckboxDomNode=e.target;
    ReactDOM.flushSync(()=>{
      setIsShowPasswordCheckboxChecked(e.target.checked);
    });
    let passwordTextFieldDomNode=passwordTextFieldRef.current;
    let cursorPositionToSetTo=passwordTextFieldDomNode.value.length;
    passwordTextFieldDomNode.focus();
    passwordTextFieldDomNode.setSelectionRange(cursorPositionToSetTo,cursorPositionToSetTo);
  }

  function handleSubmitForLoginForm(dataObj) {
    let loginProcessingModalDialogDomNode=loginProcessingModalDialogRef.current;
    loginProcessingModalDialogDomNode.showModal();

    dataObj.userNameOrEmail=dataObj.userNameOrEmail.trim();
    dataObj.password=dataObj.password.trim();
    console.log(dataObj);
    /* Send login request to rest api and if the username/email and password is valid then send the user to the todo page and also get the jwt access token and store it. If the credentials are invalid then show the invalid login dialog to the user. In both of these cases the login processing dialog should be closed */
    // fetch("http:localhost:8080/auth/v1/login",{

    // })

    

    
    
    //temporary placeholder
    setTimeout(() => {
      //assume that the credentials were invalid
      loginProcessingModalDialogDomNode.close();
      invalidLoginDialogRef.current.show();
    }, 4000);


  }

  function handleClickForInvalidLoginDialogCloseButton(e) {
    let invalidLoginDialogDomNode=invalidLoginDialogRef.current;
    invalidLoginDialogDomNode.close();
  }

  function handleClickForNetworkErrorDialogCloseButton(e) {
    let networkErrorDialogDomNode=networkErrorDialogRef.current;
    networkErrorDialogDomNode.close();
  }

  function handleClickForServerErrorDialogCloseButton(e) {
    let serverErrorDialogDomNode=serverErrorDialogRef.current;
    serverErrorDialogDomNode.close();
  }

  return (
    <>
      <form className={loginFormStylesObj.loginForm}
      onSubmit={handleSubmit(handleSubmitForLoginForm)} >
        <div className={loginFormStylesObj.loginFormHeader}>Login</div>
        <div className={loginFormStylesObj.labelAndTextFieldsWrapper}>
          <label htmlFor="userNameOrEmailTextField" className={loginFormStylesObj.labelForTextField}>Username/email:</label>
          <div>
            <input id="userNameOrEmailTextField" 
            className={classNamesForUserNameOrEmailTextField} {...register("userNameOrEmail",userNameOrEmailTextFieldValidationRules)}> 
            </input>
            {errors.userNameOrEmail
            && <div>{errors.userNameOrEmail.message}</div>}
          </div>
          
          <label htmlFor="passwordTextField" className={loginFormStylesObj.labelForTextField}>Password:</label>
          <div>
            <input id="passwordTextField" type={typeForPasswordTextField} 
            className={classNamesForPasswordTextField} {...registerWithReactHookFormObj}
            ref={(passwordTextFieldDomNode)=>{
              reactHookFormsInternalRef(passwordTextFieldDomNode);
              passwordTextFieldRef.current=passwordTextFieldDomNode;
            }}
            > 
            </input>
            {errors.password
            && <div>{errors.password.message}</div>}
          </div>  
        </div>
        

        <div className={loginFormStylesObj.showPasswordCheckboxAndLabelWrapper}>
          <input id="showPasswordCheckbox" type="checkbox" className={loginFormStylesObj.showPasswordCheckbox} onChange={handleChangeForShowPasswordCheckbox}></input>
          <label htmlFor="showPasswordCheckbox" className={loginFormStylesObj.showPasswordCheckboxLabel}>Show password</label>
        </div>

        <div className={loginFormStylesObj.formSubmitLoginButtonWrapper}>
          <button className={loginFormStylesObj.formSubmitLoginButton}>Login</button>
        </div>

      </form>

      <div className={loginFormStylesObj.userHelperTextsWrapper}>
        <p>If you don't have an account then&nbsp;
          <Link className={loginFormStylesObj.signUpUserHelperLink}>click here to sign up for an account.</Link>
        </p>
        <p className={loginFormStylesObj.forgotPasswordUserHelperText}>If you have&nbsp;  
          <Link className={loginFormStylesObj.forgotPasswordHelperLink}>forgotten your password then click here.</Link>
        </p>
      </div>
      
      
      <dialog ref={loginProcessingModalDialogRef}  className={loginFormStylesObj.loginProcessingModalDialog} closedby="none">
        <div className={loginFormStylesObj.loginProcessingSpinner}>

        </div>
      </dialog>

      <div className={loginFormStylesObj.dialogFontSizeSetterWrapper}>
        <dialog ref={invalidLoginDialogRef} className={loginFormStylesObj.invalidLoginDialog}
        closedby="any">
          <div className={loginFormStylesObj.dialogCloseButtonWrapper}>
            <button className={loginFormStylesObj.dialogCloseButton} onClick={handleClickForInvalidLoginDialogCloseButton}></button>
          </div>
          <p className={loginFormStylesObj.invalidLoginDialogText}>Provided username/email or password is invalid!</p>
          <div className={loginFormStylesObj.invalidLoginDialogImageWrapper}>
            <span className={loginFormStylesObj.invalidLoginDialogImage}></span>
          </div>
        </dialog>

        <dialog  ref={networkErrorDialogRef} className={loginFormStylesObj.networkOrServerErrorDialog} closedby="any">
          <div className={loginFormStylesObj.dialogCloseButtonWrapper}>
            <button className={loginFormStylesObj.dialogCloseButton} onClick={handleClickForNetworkErrorDialogCloseButton}></button>
          </div>
          <p className={loginFormStylesObj.networkOrServerErrorDialogText}>Network error: please check your network connection!</p>
        </dialog>

        <dialog ref={serverErrorDialogRef} className={loginFormStylesObj.networkOrServerErrorDialog} closedby="any">
          <div className={loginFormStylesObj.dialogCloseButtonWrapper}>
            <button className={loginFormStylesObj.dialogCloseButton} onClick={handleClickForServerErrorDialogCloseButton}></button>
          </div> 
          <p className={loginFormStylesObj.networkOrServerErrorDialogText}>Server error!</p>
        </dialog>
      </div>
    </>
    
    
  );
}

let userNameOrEmailTextFieldValidationRules={
  required: {
    value: true,
    message: "Please enter a user name or email"
  },
  validate: validateUserNameOrEmailTextField
};

function validateUserNameOrEmailTextField(value) {
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