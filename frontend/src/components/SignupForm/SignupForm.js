import * as React from "react";
import * as ReactDOM from "react-dom";
import * as signupFormStylesObj from "./SignupForm.css";
import {useForm} from "react-hook-form";
import {PhoneNumberUtil,PhoneNumber} from "google-libphonenumber";
import { Link } from "react-router";

export default function SignupForm() {
  let {register,handleSubmit,getValues,formState : {errors},formState,trigger}=useForm({
    defaultValues: {
      gender: "",
    }
  });
  let countryCallingCodesDropDownRef=React.useRef();

  let [isShowPasswordCheckboxChecked,setIsShowPasswordCheckboxChecked]=React.useState(false);
  let passwordTextFieldRef=React.useRef(null);
  let confirmPasswordTextFieldRef=React.useRef(null);

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

  function handleSubmitForSignupForm(data) {
    /* note todo- .delete confirmPassword key from data object.
    .modify data.phoneNumber to contain the string selectDropDownValue+data.phoneNumber. */
    console.log(data);

  }

  function handleChangeForShowPasswordCheckbox(e) {
    setIsShowPasswordCheckboxChecked(e.target.checked);
  }

  function handleChangeForCountryCodeDropDown(e) {
    if(formState.isSubmitted)
      trigger("phoneNumber")
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

      

      <div className={signupFormStylesObj.formSubmitCreateAccountButtonWrapper}>
        <button className={signupFormStylesObj.formSubmitCreateAccountButton}>Create account</button>
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


