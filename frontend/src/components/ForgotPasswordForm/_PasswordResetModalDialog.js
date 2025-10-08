import * as React from "react";
import * as passwordResetModalDialogStylesObj from "./_PasswordResetModalDialog.css";
import { useForm } from "react-hook-form";

export default function PasswordResetModalDialog({parent_setIsPasswordResetModalDialogToBeShown,parent_setIsTextModalDialogToBeShown,parent_setTextModalDialogText,parent_setIsTextDialogToBeShown,parent_setTextDialogText,parent_loadingModalDialogRef,
parent_passwordResetTokenRef
}) {

  let {register,handleSubmit,getValues,formState : {errors}}=useForm();
  let passwordResetModalDialogRef=React.useRef(null);
  let [isShowPasswordCheckboxChecked,setIsShowPasswordCheckboxChecked]=React.useState(false);
  
  React.useEffect(()=>{
    passwordResetModalDialogRef.current.showModal();
  });

  let classNamesForNewPasswordTextField=passwordResetModalDialogStylesObj.textField;
  if(errors.newPassword!==undefined) {
    classNamesForNewPasswordTextField=`${classNamesForNewPasswordTextField} ${passwordResetModalDialogStylesObj.errorStateForTextField}`;
  }

  let classNamesForConfirmNewPasswordTextField=passwordResetModalDialogStylesObj.textField;
  if(errors.confirmNewPassword!==undefined) {
    classNamesForConfirmNewPasswordTextField=`${classNamesForConfirmNewPasswordTextField} ${passwordResetModalDialogStylesObj.errorStateForTextField}`;
  }

  let typeForPasswordTextField;
  if(isShowPasswordCheckboxChecked) {
    typeForPasswordTextField="text";
  } else {
    typeForPasswordTextField="password";
  }

  function handleChangeForShowPasswordCheckbox(e) {
    setIsShowPasswordCheckboxChecked(e.target.checked);
  }

  function handleClickForCloseButton(e) {
    parent_setIsPasswordResetModalDialogToBeShown(false);
  }

  function handleSubmitForPasswordResetForm(data) {

  }

  return (
    <dialog className={passwordResetModalDialogStylesObj.passwordResetModalDialog}ref={passwordResetModalDialogRef}>
      <div className={passwordResetModalDialogStylesObj.headerAndCloseButtonWrapper}>
        <span className={passwordResetModalDialogStylesObj.header}>Password reset</span>
        <button className={passwordResetModalDialogStylesObj.closeButton} onClick={handleClickForCloseButton}></button>
      </div>
      <div className={passwordResetModalDialogStylesObj.messageToUser}>Verification of Otp's was successful. Note-Resetting your password will cause you to be logged out of all your current login's</div>
      <form onSubmit={handleSubmit(handleSubmitForPasswordResetForm)}>
        <div className={passwordResetModalDialogStylesObj.labelAndTextFieldsWrapper}>
          <label htmlFor="newPassword" className={passwordResetModalDialogStylesObj.labelForTextField}>New password:</label>
          <div>
            <input id="newPassword" type={typeForPasswordTextField} className={classNamesForNewPasswordTextField} {...register("newPassword",newPasswordTextFieldValidationRules)}></input>
            {errors.newPassword && <div>{errors.newPassword.message}</div>}
          </div>
          <label htmlFor="confirmNewPassword" className={passwordResetModalDialogStylesObj.labelForTextField}>Confirm new password:</label>
          <div>
            <input id="confirmNewPassword" type={typeForPasswordTextField} className={classNamesForConfirmNewPasswordTextField}
            {...register("confirmNewPassword",{ validate: (value)=>{
              let newPasswordTextFieldCurrentValue=getValues("newPassword");
              return validateConfirmNewPasswordTextField(value,newPasswordTextFieldCurrentValue);}
              })
            }></input>
            {errors.confirmNewPassword && <div>{errors.confirmNewPassword.message}</div>}
          </div>
        </div>
        <div className={passwordResetModalDialogStylesObj.showPasswordCheckboxAndLabelWrapper}>
          <input id="showPassword" type="checkbox" 
          className={
            passwordResetModalDialogStylesObj.showPasswordCheckbox
          } onChange={handleChangeForShowPasswordCheckbox}></input>
          <label htmlFor="showPassword" className={passwordResetModalDialogStylesObj.showPasswordCheckboxLabel}>Show password</label>
        </div>
        <div className={passwordResetModalDialogStylesObj.formSubmitResetPasswordButtonWrapper}>
          <button className={passwordResetModalDialogStylesObj.formSubmitResetPasswordButton}>Reset password</button>
        </div>
      </form>

      <div className={passwordResetModalDialogStylesObj.rulesHeaderAndTextWrapper}>
        <div>
          <span className={passwordResetModalDialogStylesObj.rulesHeaderText}>Password rules:-</span>
        </div>
        <ul className={passwordResetModalDialogStylesObj.rulesTextWrapperList}>
          <li>It needs to have a minimum of 8 characters.</li>
          <li>The maximum no of allowed characters is 128.</li>
          <li>It can contain any character, even characters from languages other than English.</li>
          <li>Spaces and tabs are not allowed.</li>
        </ul>
      </div>
    </dialog>
  );
}

let newPasswordTextFieldValidationRules={
  required: {
    value: true,
    message: "Please enter the new password"
  },
  validate: validateNewPassword
};

function validateNewPassword(value) {
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

function validateConfirmNewPasswordTextField(value, newPasswordTextFieldCurrentValue) {
  let trimmedValue=value.trim();
  newPasswordTextFieldCurrentValue=newPasswordTextFieldCurrentValue.trim();
  if(trimmedValue==="" && newPasswordTextFieldCurrentValue==="") {
    let errorMessage="Please enter the new password in the above field and then re-enter the same password here for confirmation";
    return errorMessage;
  }

  if(trimmedValue==="" && newPasswordTextFieldCurrentValue!=="") {
    let errorMessage="Please enter the exact same password again for confirmation";
    return errorMessage;
  }

  if(trimmedValue!==newPasswordTextFieldCurrentValue) {
    let errorMessage="Does not match the new password field value!";
    return errorMessage;
  }

  return true;

}
