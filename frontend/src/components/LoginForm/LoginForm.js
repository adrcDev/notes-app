import React from "react";
import ReactDOM from "react-dom";
import * as loginFormStylesObj from "./LoginForm.css";
import {useForm} from "react-hook-form";
import { Link } from "react-router";
import {validateUserNameOrEmailTextField} from "./LoginForm.internal.js"
import { JwtAccessTokenContext } from "../../contexts/JwtAcessTokenContext.js";
import TextDialog from "./_TextDialog.js";

export default function LoginForm() {
  let {register,handleSubmit,getValues,formState : {errors}}=useForm();
  let [isShowPasswordCheckboxChecked,setIsShowPasswordCheckboxChecked]=React.useState(false);
  let [isTextDialogToBeShown,setIsTextDialogToBeShown]=React.useState(false);
  let [textDialogText,setTextDialogText]=React.useState("");

  let passwordTextFieldRef=React.useRef(null);
  let loginProcessingModalDialogRef=React.useRef(null);
  let {setJwtAccessToken: setJwtAccessToken_Parent}=React.useContext(JwtAccessTokenContext);
  

  let {ref:reactHookFormsInternalRef,...registerWithReactHookFormObj}=register("password",{
    required: { value:true,message: "Please enter the password"}
  });
  
  let classNamesForUserNameOrEmailTextField=loginFormStylesObj.textField;
  if(errors.userNameOrEmail!==undefined) {
    classNamesForUserNameOrEmailTextField=`${classNamesForUserNameOrEmailTextField} ${loginFormStylesObj.errorStateForTextField}`
  }

  let typeForPasswordTextField;
  if(isShowPasswordCheckboxChecked) {
    typeForPasswordTextField="text";
  } else {
    typeForPasswordTextField="password";
  }

  let classNamesForPasswordTextField=loginFormStylesObj.textField;
  if(errors.password!==undefined) {
    classNamesForPasswordTextField=`${classNamesForPasswordTextField} ${loginFormStylesObj.errorStateForTextField}`
  }

   function handleChangeForShowPasswordCheckbox(e) {
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
    // console.log(dataObj);
    let jsonToSend=JSON.stringify(dataObj);
    /* Send login request to rest api and if the username/email and password is valid then send the user to the todo page and also get the jwt access token and store it. If the credentials are invalid then show the invalid login dialog to the user. In both of these cases the login processing dialog should be closed */
    fetch("http://localhost:8080/auth/v1/login",{
      body: jsonToSend,
      method: "post",
      headers: {
        "Content-Type": "application/json"
      },

      credentials: "include" //only added for development
    })
    .then((response)=>{
      loginProcessingModalDialogDomNode.close();
      if(response.status===500) {
        throw new Error("500: Internal server error!");
      }

      if(response.status===401) {
        throw new Error("Provided username/email or password is invalid!");
      }
      return response.json();
    },(err)=>{
        loginProcessingModalDialogDomNode.close();
        throw new Error("Network error: Please check your network connection");
    })
    .then((parsedObjectFromJson)=>{
      let jwtAccessToken=parsedObjectFromJson["jwt access token"];
      // console.log(jwtAccessToken);
      setJwtAccessToken_Parent(jwtAccessToken);
      /* TODO-Take the user to the main home page of the todo app */
      
      loginProcessingModalDialogDomNode.close();
    },(err)=>{
      loginProcessingModalDialogDomNode.close();
      setIsTextDialogToBeShown(true);
      setTextDialogText(err.message);
    });

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
          <Link to="../signup"className={loginFormStylesObj.signUpUserHelperLink}>click here to sign up for an account.</Link>
        </p>
        <p className={loginFormStylesObj.forgotPasswordUserHelperText}>If you have&nbsp;  
          <Link to="../forgot-password" className={loginFormStylesObj.forgotPasswordHelperLink}>forgotten your password then click here.</Link>
        </p>
      </div>

      <dialog ref={loginProcessingModalDialogRef}  className={loginFormStylesObj.loginProcessingModalDialog} closedby="none">
        <div className={loginFormStylesObj.loginProcessingSpinner}>
        </div>
      </dialog>

      {isTextDialogToBeShown && <TextDialog text={textDialogText} parent_setIsTextDialogToBeShown={setIsTextDialogToBeShown}/>}
    </>
  );
}

let userNameOrEmailTextFieldValidationRules={
  required: {
    value: true,
    message: "Please enter an user name or email"
  },
  validate: validateUserNameOrEmailTextField
};





