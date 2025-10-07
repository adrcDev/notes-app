import * as React from "react";
import * as forgotPasswordFormStylesObj from "./ForgotPasswordForm.css";
import { useForm } from "react-hook-form";
import { Link } from "react-router";
import TextDialog from "./_TextDialog.js";
import TextModalDialog from "./_TextModalDialog.js";
import OtpModalDialog from "./_OtpModalDialog.js";
import LoadingModalDialog from "./_LoadingModalDialog.js";
import PasswordResetModalDialog from "./_PasswordResetModalDialog.js"; 

export default function ForgotPasswordForm() {
  let {register,handleSubmit,getValues,formState : {errors}}=useForm();

  let [isTextDialogToBeShown,setIsTextDialogToBeShown]=React.useState(false);
  let [textDialogText,setTextDialogText]=React.useState(""); 
  let [isTextModalDialogToBeShown,setIsTextModalDialogToBeShown]=React.useState(false);
  let [textModalDialogText,setTextModalDialogText]=React.useState(""); 
  let [isOtpModalDialogToBeShown,setIsOtpModalDialogToBeShown]=React.useState(false);
  let [isPasswordResetModalDialogToBeShown,setIsPasswordResetModalDialogToBeShown]=React.useState(false);

  let loadingModalDialogRef=React.useRef(null);

  let classNamesForUsernameTextField=forgotPasswordFormStylesObj.textField;
  if(errors.username!==undefined) {
      classNamesForUsernameTextField=
      `${classNamesForUsernameTextField} ${forgotPasswordFormStylesObj.errorStateForTextField}`;
  }

  let classNamesForEmailTextField=forgotPasswordFormStylesObj.textField;
  if(errors.email!==undefined) {
      classNamesForEmailTextField=`${classNamesForEmailTextField} ${forgotPasswordFormStylesObj.errorStateForTextField}`;
  }

  function handleSubmitForForgotPasswordForm(data) {
    loadingModalDialogRef.current.showModal();
    data.username=data.username.toLowerCase();
    data.email=data.email.toLowerCase();
    let dataAsJson=JSON.stringify(data);
    // console.log(dataAsJson);
    fetch("http://localhost:8080/auth/v1/forgot-password/init",{
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: dataAsJson
    })
    .then((response)=>{
      loadingModalDialogRef.current.close();
      if(response.status===500) {
        setTextDialogText("500: Internal server error!");
        setIsTextDialogToBeShown(true);  
        return;
      }

      if(response.ok) {
        setIsOtpModalDialogToBeShown(true);
      }

    },(err)=>{
      loadingModalDialogRef.current.close();
      setTextDialogText("Network error: Please check your network connection");
      setIsTextDialogToBeShown(true);
    })
  }

  return (
    <>
      <form onSubmit={handleSubmit(handleSubmitForForgotPasswordForm)}
        className={forgotPasswordFormStylesObj.forgotPasswordForm}>
        <div className={forgotPasswordFormStylesObj.forgotPasswordFormHeader}>Forgot Password</div>
        <p className={forgotPasswordFormStylesObj.userHelperText}>Please enter the username and email associated with your account in order to begin the password reset process.</p>

        <div className={forgotPasswordFormStylesObj.labelAndTextFieldsWrapper}>
          <label htmlFor="username" className={forgotPasswordFormStylesObj.labelForTextField}>Username:</label>
          <div>
            <input id="username" 
            className={classNamesForUsernameTextField} {...register("username",usernameTextFieldValidationRules)}> 
            </input>
            {errors.username
            && <div>{errors.username.message}</div>}
          </div>
          
          <label htmlFor="emailTextField" className={forgotPasswordFormStylesObj.labelForTextField}>Email:</label>
          <div>
            <input id="emailTextField"
            className={classNamesForEmailTextField} {...register("email",emailTextFieldValidationRules)}
            > 
            </input>
            {errors.email
            && <div>{errors.email.message}</div>}
          </div>  
        </div>

        <div className={forgotPasswordFormStylesObj.formSubmitContinueButtonWrapper}>
          <button className={forgotPasswordFormStylesObj.formSubmitContinueButton}>Continue</button>
        </div>  
      </form>

      <div className={forgotPasswordFormStylesObj.userHelperTextsWrapper}>
        <p>If you already have an account&nbsp; 
          <Link to="../login" className={forgotPasswordFormStylesObj.loginUserHelperLink}>click here to login</Link>
        </p>
        <p className={forgotPasswordFormStylesObj.signupUserHelperText}>If you don't have an account&nbsp; 
          <Link to="../signup" className={forgotPasswordFormStylesObj.forgotPasswordHelperLink}>click here to signup for an account</Link>
        </p>
      </div>
      

      {isTextDialogToBeShown && 
        <TextDialog text={textDialogText} parent_setIsTextDialogToBeShown={setIsTextDialogToBeShown}/>}

      {isTextModalDialogToBeShown && 
        <TextModalDialog text={textModalDialogText} parent_setIsTextModalDialogToBeShown={setIsTextModalDialogToBeShown}/>}

      {isOtpModalDialogToBeShown && 
        <OtpModalDialog 
          parent_setIsOtpModalDialogToBeShown={setIsOtpModalDialogToBeShown} 
          parent_setIsTextModalDialogToBeShown={setIsTextModalDialogToBeShown}
          parent_setTextModalDialogText={setTextModalDialogText}
          parent_setIsTextDialogToBeShown={setIsTextDialogToBeShown}
          parent_setTextDialogText={setTextDialogText}
          parent_loadingModalDialogRef={loadingModalDialogRef}
          parent_getValuesRHF={getValues}
        />}

      {isPasswordResetModalDialogToBeShown && 
      <PasswordResetModalDialog       parent_setIsPasswordResetModalDialogToBeShown={setIsPasswordResetModalDialogToBeShown}
      parent_setIsTextModalDialogToBeShown={setIsTextModalDialogToBeShown}
      parent_setTextModalDialogText={setTextModalDialogText}
      parent_setIsTextDialogToBeShown={setIsTextDialogToBeShown}
      parent_setTextDialogText={setTextDialogText}
      parent_loadingModalDialogRef={loadingModalDialogRef}
      />}  

      <LoadingModalDialog ref={loadingModalDialogRef} />

    </>
  );
}


let usernameTextFieldValidationRules={
  required: {
    value: true,
    message: "Please enter an username"
  }
};

let emailTextFieldValidationRules={
  required: {
    value: true,
    message: "Please enter an email"
  },
  validate: validateEmail
};

function validateEmail(value) {
   let trimmedValue=value.trim();
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
