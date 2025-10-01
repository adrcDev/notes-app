import * as React from "react";
import * as ReactDOM from "react-dom";
import * as signupFormStylesObj from "./SignupForm.css";
import {useForm} from "react-hook-form";
import {PhoneNumberUtil,PhoneNumber} from "google-libphonenumber";
import { data, Link } from "react-router";
import TextDialog from "./_TextDialog";
import TextModalDialog from "./_TextModalDialog";

export default function SignupForm() {
  let {register,handleSubmit,getValues,formState : {errors},formState,trigger}=useForm({
    defaultValues: {
      gender: "",
    }
  });

  let [otpDialogTimeRemainingBeforeOtpExpires,setOtpDialogTimeRemainingBeforeOtpExpires]=React.useState("5:00");
  let [isTextDialogToBeShown,setIsTextDialogToBeShown]=React.useState(false);
  let [textDialogText,setTextDialogText]=React.useState(""); 
  let [isTextModalDialogToBeShown,setIsTextModalDialogToBeShown]=React.useState(false);
  let [textModalDialogText,setTextModalDialogText]=React.useState(""); 
  let [isOtpDialogResendOtpButtonToBeDisabled,setIsOtpDialogResendOtpButtonToBeDisabled]=React.useState(true);
  let [isOtpDialogVerifyOtpButtonToBeDisabled,setIsOtpDialogVerifyOtpButtonToBeDisabled]
  =React.useState(false);
  let [isShowPasswordCheckboxChecked,setIsShowPasswordCheckboxChecked]=React.useState(false);

  let countryCallingCodesDropDownRef=React.useRef();
  let otpModalDialogRef=React.useRef();
  let intervalIdForOtpModalDialogTimerRef=React.useRef(null);
  let passwordTextFieldRef=React.useRef(null);
  let confirmPasswordTextFieldRef=React.useRef(null);
  let emailOtpInputsRef=React.useRef([]);
  let phoneSmsOtpInputsRef=React.useRef([]);
  let loadingModalDialogRef=React.useRef(null);
  let signupDataJsonRef=React.useRef(null);

  let classNamesForUserNameTextField=signupFormStylesObj.textField;
  if(errors.userName!==undefined) {
    classNamesForUserNameTextField=`${classNamesForUserNameTextField} ${signupFormStylesObj.errorStateForTextField}`;
  }

  let classNamesForEmailTextField=signupFormStylesObj.textField;
  if(errors.email!==undefined) {
    classNamesForEmailTextField=`${classNamesForEmailTextField} ${signupFormStylesObj.errorStateForTextField}`;
  }

  let classNamesForPasswordTextField=signupFormStylesObj.textField;
  if(errors.password!==undefined) {
    classNamesForPasswordTextField=`${classNamesForPasswordTextField} ${signupFormStylesObj.errorStateForTextField}`;
  }

  let classNamesForConfirmPasswordTextField=signupFormStylesObj.textField;
  if(errors.confirmPassword!==undefined) {
    classNamesForConfirmPasswordTextField=`${classNamesForConfirmPasswordTextField} ${signupFormStylesObj.errorStateForTextField}`;
  }

  let classNamesForDateOfBirthField=signupFormStylesObj.textField;  
  if(errors.dateOfBirth!==undefined) {
    classNamesForDateOfBirthField=`${classNamesForDateOfBirthField} ${signupFormStylesObj.errorStateForTextField}`;
  }

  let classNamesForGenderDropDownField=signupFormStylesObj.textField; 
  if(errors.gender!==undefined)
    classNamesForGenderDropDownField=`${classNamesForGenderDropDownField} ${signupFormStylesObj.errorStateForTextField}`;

  let classNamesForPhoneNumberTextField=signupFormStylesObj.phoneNumberTextField;
  if(errors.phoneNumber!==undefined)
    classNamesForPhoneNumberTextField=`${signupFormStylesObj.phoneNumberTextField} 
  ${signupFormStylesObj.errorStateForTextField}`;

  let classNamesForCountryCodeDropDown=signupFormStylesObj.countryCodeDropDown;
  if(errors.phoneNumber!==undefined)
    classNamesForCountryCodeDropDown=`${signupFormStylesObj.countryCodeDropDown} ${signupFormStylesObj.errorStateForTextField}`;

  let typeForPasswordTextField;
  if(isShowPasswordCheckboxChecked) {
    typeForPasswordTextField="text";
  } else {
    typeForPasswordTextField="password";
  }

  let otpDialogResendBothOtpButtonCommonPropsObj={
    className: signupFormStylesObj.resendBothOtpsButton
  };
  let otpDialogResendBothOtpButton=
    (<button {...otpDialogResendBothOtpButtonCommonPropsObj} onClick={handleClickForOtpDialogResendBothOtpButtons}>
      Resend both OTP's ↺
      </button>);
  if(isOtpDialogResendOtpButtonToBeDisabled) {
    otpDialogResendBothOtpButton=
      (<button {...otpDialogResendBothOtpButtonCommonPropsObj} disabled>
        Resend both OTP's ↺
        </button>);
  }

  let otpDialogVerifyOtpButtonCommonPropsObj={
    className: signupFormStylesObj.verifyOtpAndCreateAccountButton
  };
  let otpDialogVerifyOtpButton=
    (
      <button {...otpDialogVerifyOtpButtonCommonPropsObj}>Verify OTP's and create account</button>
    );
  if(isOtpDialogVerifyOtpButtonToBeDisabled) {
    otpDialogVerifyOtpButton=
      (
        <button {...otpDialogVerifyOtpButtonCommonPropsObj} disabled>Verify OTP's and create account</button>
      );
  }
    


  function handleSubmitForSignupForm(signupData) {
    loadingModalDialogRef.current.showModal();
    delete signupData["confirmPassword"];
    signupData.phoneNumber=`${countryCallingCodesDropDownRef.current.value}${signupData.phoneNumber}`;
    // console.log(signupData);
    let signupDataJson=JSON.stringify(signupData);
    // console.log(signupDataJson);
    /* Storing sign up data json in a ref to later use in click handler of 'resend both otps' button of the otp verification modal dialog */
    signupDataJsonRef.current=signupDataJson;
    fetch("http://localhost:8080/auth/v1/signup/init",{
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: signupDataJson,
    })
    .then((response)=>{
      loadingModalDialogRef.current.close();
      if(response.status===500) {
        setTextDialogText("500: Internal server error!");
        setIsTextDialogToBeShown(true);
        return;
      }

      if(response.status===401) {
        setTextDialogText("An user account with the provided details already exists.");
        setIsTextDialogToBeShown(true);
        return;
      }

      if(response.ok) {
        otpModalDialogRef.current.showModal();
        let intervalId=setInterval(() => {
              setOtpDialogTimeRemainingBeforeOtpExpires((prevMinutesSecondsString)=>{
                let result=subtract1SecFromMinutesSecondsString(prevMinutesSecondsString)
                if(result==="0:00") {
                  clearInterval(intervalId);
                  setIsOtpDialogResendOtpButtonToBeDisabled(false);
                  setIsOtpDialogVerifyOtpButtonToBeDisabled(true);
                }   
                return result;
              });
          }, 1000);
          intervalIdForOtpModalDialogTimerRef.current=intervalId;
      }

      
    },(err) =>{
      // console.log("fetch promise rejected callback ran");
      loadingModalDialogRef.current.close();
      setTextDialogText("Network error: Please check your network connection");
      setIsTextDialogToBeShown(true);
    });

  }

  function handleClickForOtpDialogResendBothOtpButtons(e) {
    loadingModalDialogRef.current.showModal();
    let signupDataJson=signupDataJsonRef.current;
    // console.log(signupDataJson);
    fetch("http://localhost:8080/auth/v1/signup/resend-otps",{
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: signupDataJson
    })
    .then((response)=>{
      loadingModalDialogRef.current.close();
      if(response.status===500) {
        otpModalDialogRef.current.close();
        setTextDialogText("500: Internal server error!");
        setIsTextDialogToBeShown(true);
        return;
      }

      if(response.status===401) {
        otpModalDialogRef.current.close();
        setTextDialogText("An user account with the provided details already exists.");
        setIsTextDialogToBeShown(true);
        return;
      }

      if(response.ok) {
        setTextModalDialogText("Separate new OTP's have been sent to the entered phone number and email respectively.");
        setIsTextModalDialogToBeShown(true);
        setIsOtpDialogResendOtpButtonToBeDisabled(true);
        setIsOtpDialogVerifyOtpButtonToBeDisabled(false);
        setOtpDialogTimeRemainingBeforeOtpExpires("5:00");
        let intervalId=setInterval(() => {
              setOtpDialogTimeRemainingBeforeOtpExpires((prevMinutesSecondsString)=>{
                let result=subtract1SecFromMinutesSecondsString(prevMinutesSecondsString)
                if(result==="0:00") {
                  clearInterval(intervalId);
                  setIsOtpDialogResendOtpButtonToBeDisabled(false);
                  setIsOtpDialogVerifyOtpButtonToBeDisabled(true);
                }   
                return result;
              });
          }, 1000);
          intervalIdForOtpModalDialogTimerRef.current=intervalId;
      }

    },(err)=>{
      // console.log("fetch promise rejected callback ran");
      loadingModalDialogRef.current.close();
      otpModalDialogRef.current.close();
      setTextDialogText("Network error: Please check your network connection");
      setIsTextDialogToBeShown(true);
    });
  }

  function handleSubmitForOtpDialogForm(e) {
    e.preventDefault();
    let emptyEmailOtpCharInput=findEmptyOtpCharInput(emailOtpInputsRef);
    let isAnEmailOtpCharInputEmpty=emptyEmailOtpCharInput!==null;
    if(isAnEmailOtpCharInputEmpty) {
      emptyEmailOtpCharInput.focus();
      return;
    }

    let emptyPhoneSmsOtpCharInput=findEmptyOtpCharInput(phoneSmsOtpInputsRef);
    let isAnPhoneSmsOtpCharInputEmpty=emptyPhoneSmsOtpCharInput!==null;
    if(isAnPhoneSmsOtpCharInputEmpty) {
      emptyPhoneSmsOtpCharInput.focus();
      return;
    }

    loadingModalDialogRef.current.showModal();
    let otpVerificationDataObj={
      username: getValues("userName"),
      phoneNumberOtp: getOtpStringFromOtpCharInputsRef(phoneSmsOtpInputsRef),
      emailOtp: getOtpStringFromOtpCharInputsRef(emailOtpInputsRef)
    };

    let otpVerificationDataJson=JSON.stringify(otpVerificationDataObj);
    fetch("http://localhost:8080/auth/v1/signup/verify-otps",{
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body:otpVerificationDataJson
    })
    .then((response)=>{
      loadingModalDialogRef.current.close();
      if(response.status===500) {
        otpModalDialogRef.current.close();
        setTextDialogText("500: Internal server error!");
        setIsTextDialogToBeShown(true);
        return;
      }

      if(response.status===401) {
        setTextModalDialogText("One or both of the OTP's are invalid");
        setIsTextModalDialogToBeShown(true);
        return;
      }

      if(response.ok) {
        otpModalDialogRef.current.close();
        let accountCreationSuccessMessageJsxObj=
          (<>
            User account successfully created. You can now login with this account. <Link className={signupFormStylesObj.loginPageLinkText} to="../login">Click here to go to the login page.</Link>
          </>);
        setTextDialogText(accountCreationSuccessMessageJsxObj);
        setIsTextDialogToBeShown(true);
      }

    },(err)=>{
      // console.log("fetch promise rejected callback ran");
      loadingModalDialogRef.current.close();
      otpModalDialogRef.current.close();
      setTextDialogText("Network error: Please check your network connection");
      setIsTextDialogToBeShown(true);
    })


  }

  function handleChangeForShowPasswordCheckbox(e) {
    setIsShowPasswordCheckboxChecked(e.target.checked);
  }

  function handleChangeForCountryCodeDropDown(e) {
    if(formState.isSubmitted)
      trigger("phoneNumber")
  }

  function handleClickForOtpDialogCloseButton(e) {
    let otpModalDialogDomNode=otpModalDialogRef.current;
    otpModalDialogDomNode.close();
  }

  function handleKeyDownForEmailOtpCharEntry(e,index) {
    let emailOtpInputsDomNodesArr=emailOtpInputsRef.current;
    if(e.key==="ArrowLeft") {
      if(index===0) {
        let prevEmailOtpEntryDomNode=emailOtpInputsDomNodesArr[emailOtpInputsDomNodesArr.length-1];
        prevEmailOtpEntryDomNode.focus();
        let valueLengthOfPrevEmailOtpEntryDomNode=prevEmailOtpEntryDomNode.value.length;
        setTimeout(() => {
          prevEmailOtpEntryDomNode.setSelectionRange(valueLengthOfPrevEmailOtpEntryDomNode,valueLengthOfPrevEmailOtpEntryDomNode);  
        }, 0);
      }
      else {
        let prevEmailOtpEntryDomNode=emailOtpInputsDomNodesArr[index-1];
        prevEmailOtpEntryDomNode.focus();
        let valueLengthOfPrevEmailOtpEntryDomNode=prevEmailOtpEntryDomNode.value.length;
        setTimeout(() => {
          prevEmailOtpEntryDomNode.setSelectionRange(valueLengthOfPrevEmailOtpEntryDomNode,valueLengthOfPrevEmailOtpEntryDomNode);  
        }, 0);
        
      }
      return;
    }

    if(e.key==="ArrowRight") {
      if(index===emailOtpInputsDomNodesArr.length-1) {
        let nextEmailOtpEntryDomNode=emailOtpInputsDomNodesArr[0];
        nextEmailOtpEntryDomNode.focus();
      } 
      else {
          let nextEmailOtpEntryDomNode=emailOtpInputsDomNodesArr[index+1];
        nextEmailOtpEntryDomNode.focus()
      }
      return;
    }

    if(e.key==="Backspace") {
      let currentlyFocusedEmailOtpDomNode=e.target;
      if(currentlyFocusedEmailOtpDomNode.value.length===0) {
        let emailOtpInputsDomNodesArr=emailOtpInputsRef.current;
        let prevEmailOtpEntryDomNode;
        if(index===0)
          prevEmailOtpEntryDomNode=emailOtpInputsDomNodesArr[emailOtpInputsDomNodesArr.length-1];
        else
          prevEmailOtpEntryDomNode=emailOtpInputsDomNodesArr[index-1];
        setTimeout(()=>prevEmailOtpEntryDomNode.focus(),0);
      } 
      return;
    }
  }

  function handleBeforeInputForEmailOtpCharEntry(e,index) {
    let charThatIsAboutToBeInserted=e.data;
    let regexForAllowedCharacter=/^[a-zA-Z0-9]$/;
    let isEnteredCharacterAlphaNumeric=regexForAllowedCharacter.test(charThatIsAboutToBeInserted);
    if(!isEnteredCharacterAlphaNumeric)
      e.preventDefault();
    else {
      let emailOtpInputsDomNodesArr=emailOtpInputsRef.current;
      let nextEmailOtpEntryDomNode;
      if(index+1===emailOtpInputsDomNodesArr.length)
        nextEmailOtpEntryDomNode=emailOtpInputsDomNodesArr[0];
      else
        nextEmailOtpEntryDomNode=emailOtpInputsDomNodesArr[index+1];
      if(nextEmailOtpEntryDomNode.value.length===0)
        setTimeout(()=>nextEmailOtpEntryDomNode.focus(),0);
    }
  }

  function handlePasteForEmailOtpCharEntry(e) {
    e.preventDefault();
    let pastedText=e.clipboardData.getData("text").trim();
    let regexForValidOTP=/^[a-zA-Z0-9]{6}$/;
    let isPastedTextValidOTP=regexForValidOTP.test(pastedText);
    if(isPastedTextValidOTP) {
      let emailOtpInputsDomNodesArr=emailOtpInputsRef.current;
      for(let i=0;i<emailOtpInputsDomNodesArr.length;i++) {
        emailOtpInputsDomNodesArr[i].value=pastedText.charAt(i);
      }
    }
  }

  function handleKeyDownForPhoneSmsOtpCharEntry(e,index) {
    let phoneSmsOtpInputsDomNodesArr=phoneSmsOtpInputsRef.current;
    if(e.key==="ArrowLeft") {
      let prevPhoneSmsOtpEntryDomNode;
      if(index===0) {
        prevPhoneSmsOtpEntryDomNode=phoneSmsOtpInputsDomNodesArr[phoneSmsOtpInputsDomNodesArr.length-1];
      }
      else {
        prevPhoneSmsOtpEntryDomNode=phoneSmsOtpInputsDomNodesArr[index-1];
      }

      prevPhoneSmsOtpEntryDomNode.focus();
      let valueLengthOfPrevPhoneSmsOtpEntryDomNode=prevPhoneSmsOtpEntryDomNode.value.length;
      setTimeout(() => {
        prevPhoneSmsOtpEntryDomNode.setSelectionRange(valueLengthOfPrevPhoneSmsOtpEntryDomNode,valueLengthOfPrevPhoneSmsOtpEntryDomNode);  
      }, 0);
      return;
    }

    if(e.key==="ArrowRight") {
      if(index===phoneSmsOtpInputsDomNodesArr.length-1) {
        let nextPhoneSmsOtpEntryDomNode=phoneSmsOtpInputsDomNodesArr[0];
        nextPhoneSmsOtpEntryDomNode.focus();
      } 
      else {
          let nextPhoneSmsOtpEntryDomNode=phoneSmsOtpInputsDomNodesArr[index+1];
          nextPhoneSmsOtpEntryDomNode.focus()
      }
      return;
    }

    if(e.key==="Backspace") {
      let currentlyFocusedPhoneSmsOtpDomNode=e.target;
      if(currentlyFocusedPhoneSmsOtpDomNode.value.length===0) {
        let phoneSmsOtpInputsDomNodesArr=phoneSmsOtpInputsRef.current;
        let prevPhoneSmsOtpEntryDomNode;
        if(index===0)
          prevPhoneSmsOtpEntryDomNode=phoneSmsOtpInputsDomNodesArr[phoneSmsOtpInputsDomNodesArr.length-1];
        else
          prevPhoneSmsOtpEntryDomNode=phoneSmsOtpInputsDomNodesArr[index-1];
        setTimeout(()=>prevPhoneSmsOtpEntryDomNode.focus(),0);
      } 
      return;
    }
  }

  function handleBeforeInputForPhoneSmsOtpCharEntry(e,index) {
    let charThatIsAboutToBeInserted=e.data;
    let regexForAllowedCharacter=/^[a-zA-Z0-9]$/;
    let isEnteredCharacterAlphaNumeric=regexForAllowedCharacter.test(charThatIsAboutToBeInserted);
    if(!isEnteredCharacterAlphaNumeric)
      e.preventDefault();
    else {
      let phoneSmsOtpInputsDomNodesArr=phoneSmsOtpInputsRef.current;
      let nextPhoneSmsOtpEntryDomNode;
      if(index+1===phoneSmsOtpInputsDomNodesArr.length)
        nextPhoneSmsOtpEntryDomNode=phoneSmsOtpInputsDomNodesArr[0];
      else
        nextPhoneSmsOtpEntryDomNode=phoneSmsOtpInputsDomNodesArr[index+1];
      if(nextPhoneSmsOtpEntryDomNode.value.length===0)
        setTimeout(()=>nextPhoneSmsOtpEntryDomNode.focus(),0);
    }
  }

  function handlePasteForPhoneSmsOtpCharEntry(e) {
    e.preventDefault();
    let pastedText=e.clipboardData.getData("text").trim();
    let regexForValidOTP=/^[a-zA-Z0-9]{6}$/;
    let isPastedTextValidOTP=regexForValidOTP.test(pastedText);
    if(isPastedTextValidOTP) {
      let phoneSmsOtpInputsDomNodesArr=phoneSmsOtpInputsRef.current;
      for(let i=0;i<phoneSmsOtpInputsDomNodesArr.length;i++) {
        phoneSmsOtpInputsDomNodesArr[i].value=pastedText.charAt(i);
      }
    }
  }

  function handleCloseForOtpModalDialog(e) {
    let intervalIdForOtpModalDialogTimer=intervalIdForOtpModalDialogTimerRef.current;
    clearInterval(intervalIdForOtpModalDialogTimer);
    setOtpDialogTimeRemainingBeforeOtpExpires("5:00");
    clearOtpCharInputs(emailOtpInputsRef);
    clearOtpCharInputs(phoneSmsOtpInputsRef);
    setIsOtpDialogResendOtpButtonToBeDisabled(true);
    setIsOtpDialogVerifyOtpButtonToBeDisabled(false);
  }

  

  
  
  return (
    <>
    <form className={signupFormStylesObj.signupForm}
     onSubmit={handleSubmit(handleSubmitForSignupForm)}>
      <div className={signupFormStylesObj.signupFormHeader}>Sign up</div>
      <div className={signupFormStylesObj.labelAndTextFieldsWrapper} >
        <label htmlFor="userNameTextField" className={signupFormStylesObj.labelForTextField}>Username:</label>
        <div>
            <input id="userNameTextField" 
            className={classNamesForUserNameTextField} {...register("userName",userNameTextFieldValidationRules)}> 
            </input>
            {errors.userName
            && <div>{errors.userName.message}</div>}
        </div>

        <label htmlFor="emailTextField" className={signupFormStylesObj.labelForTextField}>Email:</label>
        <div>
            <input id="emailTextField" 
            className={classNamesForEmailTextField} {...register("email",emailTextFieldValidationRules)}> 
            </input>
            {errors.email
            && <div>{errors.email.message}</div>}
        </div>

        <label htmlFor="passwordTextField" className={signupFormStylesObj.labelForTextField}>Password:</label>
        <div>
            <input id="passwordTextField" type={typeForPasswordTextField}
            className={classNamesForPasswordTextField} ref={passwordTextFieldRef}
            {...register("password",passwordTextFieldValidationRules)}> 
            </input>
            {errors.password
            && <div>{errors.password.message}</div>}
        </div>

        <label htmlFor="confirmPasswordTextField" className={signupFormStylesObj.labelForTextField}>Confirm password:</label>
        <div>
            <input id="confirmPasswordTextField" type={typeForPasswordTextField}
            className={classNamesForConfirmPasswordTextField} ref={confirmPasswordTextFieldRef}
            {...register("confirmPassword",{ validate: (value)=>{
              let passwordTextFieldCurrentValue=getValues("password");
              return validateConfirmPasswordTextField(value,passwordTextFieldCurrentValue);}
              })
            }> 
            </input>
            {errors.confirmPassword
            && <div>{errors.confirmPassword.message}</div>}
        </div>
      </div>

      <div className={signupFormStylesObj.showPasswordCheckboxAndLabelWrapper}>
        <input id="showPasswordCheckbox" type="checkbox" className={signupFormStylesObj.showPasswordCheckbox} onChange={handleChangeForShowPasswordCheckbox}></input>
        <label htmlFor="showPasswordCheckbox" className={signupFormStylesObj.showPasswordCheckboxLabel}>Show password</label>
      </div>

      <div className={signupFormStylesObj.labelAndTextFieldsWrapper} >
        <label htmlFor="dateOfBirthField" className={signupFormStylesObj.labelForTextField}>Date of birth:</label>
        <div>
          <input id="dateOfBirthField" type="date" className={classNamesForDateOfBirthField}{...register("dateOfBirth",dateOfBirthFieldValidationRules)} max={getTodaysDate()}min={getDate160yearsFromToday()}></input>
          {errors.dateOfBirth
            && <div>{errors.dateOfBirth.message}</div>}
        </div>

        <label htmlFor="genderDropDownField" className={signupFormStylesObj.labelForTextField}>Gender:</label>
        <div>
          <select id="genderDropDownField" {...register("gender",genderDropDownFieldValidationRules)} className={classNamesForGenderDropDownField}>
            <option value="" disabled>Select your gender</option>
            <option value="male">male</option>
            <option value="female">female</option>
            <option value="other">other</option>
            <option value="rather not say">rather not say</option>
          </select>
          {errors.gender
            && <div>{errors.gender.message}</div>}
        </div>

        <label htmlFor="phoneNumberTextField" className={signupFormStylesObj.labelForTextField} >Phone number:</label>
        <div className={signupFormStylesObj.phoneNumberLabelAndFormControlsWrapper}>
          <select ref={countryCallingCodesDropDownRef} 
          onChange={handleChangeForCountryCodeDropDown}
          className={classNamesForCountryCodeDropDown}>
            {PhoneNumberUtil.getInstance().getSupportedCallingCodes().sort((a,b)=>a-b).map((ele,index)=>
              (<option key={index}>+{ele}</option>))}
          </select>
          <input id="phoneNumberTextField" type="tel"
            className={classNamesForPhoneNumberTextField} {...register("phoneNumber",{
              required: {
               value: true,
               message: "Pleae enter your phone number"
              },
              validate: (value)=>{
                let countryCodeDropDownValue=countryCallingCodesDropDownRef.current.value;
                return validatePhoneNumber(value,countryCodeDropDownValue);
              }
})}></input>
          {errors.phoneNumber
            && <div>{errors.phoneNumber.message}</div>}
        </div>


      </div>

      

      <div className={signupFormStylesObj.formSubmitContinueButtonWrapper}>
        <button className={signupFormStylesObj.formSubmitContinueButton}>Continue</button>
      </div>
    </form>

    <div className={signupFormStylesObj.userHelperTextsWrapper}>
      <p>If you already have an account&nbsp; 
        <Link to="../login" className={signupFormStylesObj.loginUserHelperLink}>click here to login</Link>
      </p>
      <p className={signupFormStylesObj.forgotPasswordUserHelperText}>If you already have an account but&nbsp; 
        <Link className={signupFormStylesObj.forgotPasswordHelperLink}>forgotten your password then click here</Link>
      </p>
      
      <div className={signupFormStylesObj.rulesHeaderAndTextWrapper}>
        <div>
          <span className={signupFormStylesObj.rulesHeaderText}>Username rules:-</span>
        </div>
        <ul className={signupFormStylesObj.rulesTextWrapperList}>
          <li>It needs to have minimum of 3 characters.</li>
          <li>The maximum no of allowed characters is 30.</li>
          <li>The only allowed characters are a-z, A-Z, 0-9, .(dot), _(underscore) and -(hyphen).</li>
          <li>.(dot), _(underscore) and -(hyphen) are not allowed as the starting or ending character.</li>
          <li>Consecutive .(dot), _(underscore) and -(hyphen) or any combination of these characters are not allowed.</li>
        </ul>
      </div>

      <div className={signupFormStylesObj.rulesHeaderAndTextWrapper}>
        <div>
          <span className={signupFormStylesObj.rulesHeaderText}>Password rules:-</span>
        </div>
        <ul className={signupFormStylesObj.rulesTextWrapperList}>
          <li>It needs to have a minimum of 8 characters.</li>
          <li>The maximum no of allowed characters is 128.</li>
          <li>It can contain any character, even characters from languages other than English.</li>
          <li>Spaces and tabs are not allowed.</li>
        </ul>
      </div>

    </div>

    <dialog ref={otpModalDialogRef} className={signupFormStylesObj.otpDialog} closedby="closerequest" onClose={handleCloseForOtpModalDialog}>
      <div className={signupFormStylesObj.otpdialogHeaderTextAndCloseButtonWrapper}>
        <span className={signupFormStylesObj.otpDialogHeaderText}>OTP verification</span>
        <button className={signupFormStylesObj.dialogCloseButton} onClick={handleClickForOtpDialogCloseButton}></button>
      </div>
      <form onSubmit={handleSubmitForOtpDialogForm}>
        <p>This website has been created for learning purposes and does not actually send otp to your email and phone no. The valid OTP is always <strong>100000</strong>.</p>            
        <p className={signupFormStylesObj.emailOtpLabel}>Please enter the otp received through email:</p> 
        <div className={signupFormStylesObj.otpEntriesWrapper}>
          {[0,1,2,3,4,5].map((index)=>
            (
              <input key={index} className={signupFormStylesObj.otpCharEntry} maxLength="1"
                onKeyDown={(e)=>handleKeyDownForEmailOtpCharEntry(e,index)}
                onBeforeInput={(e)=>handleBeforeInputForEmailOtpCharEntry(e,index)}
                onPaste={handlePasteForEmailOtpCharEntry}
                ref={(node)=>{
                  let emailOtpInputsDomNodesArr=emailOtpInputsRef.current;
                  emailOtpInputsDomNodesArr[index]=node;
                  return ()=>{
                    delete emailOtpInputsDomNodesArr[index];
                  }
                }}>
                
              </input>
            ))
          }
        </div>
        <p className={signupFormStylesObj.phoneSmsOtpLabel}>Please enter the otp received through phone sms:</p>
        <div className={signupFormStylesObj.otpEntriesWrapper}>
          {[0,1,2,3,4,5].map((index)=>
            (
              <input key={index} className={signupFormStylesObj.otpCharEntry} maxLength="1"
                onKeyDown={(e)=>handleKeyDownForPhoneSmsOtpCharEntry(e,index)}
                onBeforeInput={(e)=>handleBeforeInputForPhoneSmsOtpCharEntry(e,index)}
                onPaste={handlePasteForPhoneSmsOtpCharEntry}
                ref={(node)=>{
                  let phoneSmsOtpInputsDomNodesArr=phoneSmsOtpInputsRef.current;
                  phoneSmsOtpInputsDomNodesArr[index]=node;
                  return ()=>{
                    delete phoneSmsOtpInputsDomNodesArr[index];
                  }
                }}>
                
              </input>
            ))
          }
        </div>
        <div className={signupFormStylesObj.otpTimeRemainingToExpireIndicator}>
          Both of the OTP's expire in {otpDialogTimeRemainingBeforeOtpExpires}
        </div>

        {otpDialogResendBothOtpButton}

        <div className={signupFormStylesObj.verifyOtpAndCreateAccountButtonWrapper}>
          {otpDialogVerifyOtpButton}
        </div>
      </form>
    </dialog>

    <dialog ref={loadingModalDialogRef}  className={signupFormStylesObj.loginProcessingModalDialog} closedby="none">
      <div className={signupFormStylesObj.loadingSpinner}>
      </div>
    </dialog>
    

    {isTextDialogToBeShown && <TextDialog text={textDialogText} parent_setIsTextDialogToBeShown={setIsTextDialogToBeShown}/>}

    {isTextModalDialogToBeShown && <TextModalDialog text={textModalDialogText} parent_setIsTextModalDialogToBeShown={setIsTextModalDialogToBeShown}/>}
    </>


  );

}

let userNameTextFieldValidationRules={
  required: {
    value: true,
    message: "Please enter a user name"
  },
  validate: validateUserName
};

let emailTextFieldValidationRules={
  required: {
    value: true,
    message: "Please enter a email"
  },
  validate: validateEmail
};

let passwordTextFieldValidationRules={
  required: {
    value: true,
    message: "Please enter a password"
  },
  validate: validatePassword
};

let dateOfBirthFieldValidationRules={
  required: {
    value: true,
    message: "Please select your date of birth"
  }
};

let genderDropDownFieldValidationRules={
  required: {
    value: true,
    message: "Please select your gender"
  }
};


function validateUserName(value) {
  let trimmedValue=value.trim();
  if(isValidUserName(trimmedValue)) {
    return true;
  }
  else {
    let errorMessage="Invalid username! Please read the user name rules below";
    return errorMessage;
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

function validatePassword(value) {
  let trimmedValue=value.trim();
  let passwordRegex=/^[^\s]{8,128}$/u;
  let inputIsValidPassword=passwordRegex.test(trimmedValue);
  if(inputIsValidPassword) {
    return true;
  }
  else {
    let errorMessage="Invalid password! Please read the password rules below";
    return errorMessage;
  }

}

function validateConfirmPasswordTextField(value,passwordTextFieldCurrentValue) {
  let trimmedValue=value.trim();
  passwordTextFieldCurrentValue=passwordTextFieldCurrentValue.trim();
  if(trimmedValue==="" && passwordTextFieldCurrentValue==="") {
    let errorMessage="Please enter a password in the above field and then enter the same password here for confirmation";
    return errorMessage;
  }

  if(trimmedValue==="" && passwordTextFieldCurrentValue!=="") {
    let errorMessage="Please enter the exact same password again for confirmation";
    return errorMessage;
  }

  if(trimmedValue!==passwordTextFieldCurrentValue) {
    let errorMessage="Does not match the password field value!";
    return errorMessage;
  }

  return true;

}

function validatePhoneNumber(value,countryCallingCodesDropDownValue) {
  let trimmedValue=value.trim();
  let countryCodeValue=countryCallingCodesDropDownValue.replace("+","");
  let phoneNumberUtilObj=PhoneNumberUtil.getInstance();
  let phoneNumberObj=new PhoneNumber();
  phoneNumberObj.setCountryCode(Number(countryCodeValue));
  phoneNumberObj.setNationalNumber(Number(trimmedValue));
  if(phoneNumberUtilObj.isValidNumber(phoneNumberObj)) {
    return true;
  }
  else {
    let errorMessage="Invalid phone number format!"
    return errorMessage;
  }
  
  
}

function getTodaysDate() {
  let today = new Date();

let  year = `${today.getFullYear()}`; 
let  month = `${today.getMonth() + 1}`; // 0 = Jan, so add 1
let  day = `${today.getDate()}`; 

if(month.length===1)
  month=`0${month}`;

if(day.length===1)
  day=`0${day}`;

return `${year}-${month}-${day}`;
}

function getDate160yearsFromToday() {
  let today = new Date();
  let  currentYear = `${today.getFullYear()}`; 
  let  month = `${today.getMonth() + 1}`; // 0 = Jan, so add 1
  let  day = `${today.getDate()}`; 
  
  if(month.length===1)
    month=`0${month}`;

  if(day.length===1)
    day=`0${day}`;

  return `${currentYear-160}-${month}-${day}`;

}

function subtract1SecFromMinutesSecondsString(timeString) {
  let resultMinutesPartString;
  let resultSecondsPartString;
  let minutesPartString=timeString.split(":")[0];
  let secondsPartString=timeString.split(":")[1];
  if(secondsPartString==="00") {
    resultMinutesPartString=String(Number(minutesPartString)-1);
    resultSecondsPartString="59";
  }
  else {
    resultMinutesPartString=minutesPartString;
    let resultSecondsPartAsNumber=Number(secondsPartString)-1;
    if(resultSecondsPartAsNumber<10)
      resultSecondsPartString=`0${resultSecondsPartAsNumber}`
    else
      resultSecondsPartString=String(resultSecondsPartAsNumber);
  }

  return `${resultMinutesPartString}:${resultSecondsPartString}`;
}

function findEmptyOtpCharInput(otpCharInputsRef) {
  let otpCharInputsDomNodesArr=otpCharInputsRef.current;
  for(let otpCharInputDomNode of otpCharInputsDomNodesArr) {
    if(otpCharInputDomNode.value==="")
      return otpCharInputDomNode;
  }

  return null;
}

function getOtpStringFromOtpCharInputsRef(otpCharInputsRef) {
  let otpString="";
  let otpCharInputsDomNodesArr=otpCharInputsRef.current;
  for(let otpCharInputDomNode of otpCharInputsDomNodesArr) {
    let otpCharacter=otpCharInputDomNode.value;
    otpString=`${otpString}${otpCharacter}`;
  }
  return otpString;
}

function clearOtpCharInputs(otpCharInputsRef) {
  let otpCharInputsDomNodesArr=otpCharInputsRef.current;
  for(let otpCharInputDomNode of otpCharInputsDomNodesArr) {
    otpCharInputDomNode.value="";
  }
}