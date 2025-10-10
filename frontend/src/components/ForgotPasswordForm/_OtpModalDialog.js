import * as React from "react";
import * as otpModalDialogStylesObj from "./_OtpModalDialog.css";

export default function OtpModalDialog({parent_setIsOtpModalDialogToBeShown,parent_setIsTextModalDialogToBeShown,parent_setTextModalDialogText,parent_setIsTextDialogToBeShown,parent_setTextDialogText,parent_setIsPasswordResetModalDialogToBeShown,parent_loadingModalDialogRef,parent_getValuesRHF,parent_passwordResetTokenRef
}) {
  
  let [otpDialogTimeRemainingBeforeOtpExpires,setOtpDialogTimeRemainingBeforeOtpExpires]=React.useState("5:00");
  let [isOtpDialogResendOtpButtonToBeDisabled,setIsOtpDialogResendOtpButtonToBeDisabled]=React.useState(true);
  let [isOtpDialogVerifyOtpButtonToBeDisabled,setIsOtpDialogVerifyOtpButtonToBeDisabled]
    =React.useState(false);
  

  let otpModalDialogRef=React.useRef(null);
  let emailOtpInputsRef=React.useRef([]);
  let phoneSmsOtpInputsRef=React.useRef([]);
  let intervalIdForOtpModalDialogTimerRef=React.useRef(null);
  
  React.useEffect(()=>{
    otpModalDialogRef.current.showModal();
    let intervalIdForOtpModalDialogTimer=setInterval(() => {
              setOtpDialogTimeRemainingBeforeOtpExpires((prevMinutesSecondsString)=>{
                let result=subtract1SecFromMinutesSecondsString(prevMinutesSecondsString)
                if(result==="0:00") {
                  clearInterval(intervalIdForOtpModalDialogTimer);
                  setIsOtpDialogResendOtpButtonToBeDisabled(false);
                  setIsOtpDialogVerifyOtpButtonToBeDisabled(true);
                }   
                return result;
              });
      }, 1000);
      intervalIdForOtpModalDialogTimerRef.current=intervalIdForOtpModalDialogTimer;

      return ()=>{
        clearInterval(intervalIdForOtpModalDialogTimer);
      }
  },[]);

  let otpDialogResendBothOtpButtonCommonPropsObj={
    className: otpModalDialogStylesObj.resendBothOtpsButton
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
      className: otpModalDialogStylesObj.verifyOtpAndCreateAccountButton
    };
    let otpDialogVerifyOtpButton=
      (
        <button {...otpDialogVerifyOtpButtonCommonPropsObj}>Verify OTP's to set a new password</button>
      );
    if(isOtpDialogVerifyOtpButtonToBeDisabled) {
      otpDialogVerifyOtpButton=
        (
          <button {...otpDialogVerifyOtpButtonCommonPropsObj} disabled>Verify OTP's and create account</button>
        );
    }

  function handleClickForOtpDialogCloseButton(e) {
    otpModalDialogRef.current.close();
  }

  function handleCloseForOtpModalDialog(e) {
    parent_setIsOtpModalDialogToBeShown(false);
  }

  function handleClickForOtpDialogResendBothOtpButtons(e) {
    parent_loadingModalDialogRef.current.showModal();
    let forgotPasswordDataObj={
      username: parent_getValuesRHF("username"),
      email: parent_getValuesRHF("email")
    };
    let forgotPasswordDataObjAsJson=JSON.stringify(forgotPasswordDataObj);
    fetch("http://localhost:8080/auth/v1/forgot-password/init",{
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: forgotPasswordDataObjAsJson
    })
    .then((response)=>{
      parent_loadingModalDialogRef.current.close();
      if(response.status===500) {
        parent_setTextModalDialogText("500: Internal server error");
        parent_setIsTextModalDialogToBeShown(true);
        parent_setIsOtpModalDialogToBeShown(false); 
        return;
      }

      if(response.ok) {
        parent_setTextModalDialogText("If the entered details match an user account in our system then new separate otp's have been sent to the email and phone number associated to this account");
        parent_setIsTextModalDialogToBeShown(true);
        setIsOtpDialogVerifyOtpButtonToBeDisabled(false);
        setIsOtpDialogResendOtpButtonToBeDisabled(true);
        setOtpDialogTimeRemainingBeforeOtpExpires("5:00");
        let intervalIdForOtpModalDialogTimer=setInterval(() => {
              setOtpDialogTimeRemainingBeforeOtpExpires((prevMinutesSecondsString)=>{
                let result=subtract1SecFromMinutesSecondsString(prevMinutesSecondsString)
                if(result==="0:00") {
                  clearInterval(intervalIdForOtpModalDialogTimer);
                  setIsOtpDialogResendOtpButtonToBeDisabled(false);
                  setIsOtpDialogVerifyOtpButtonToBeDisabled(true);
                }   
                return result;
              });
      }, 1000);
        intervalIdForOtpModalDialogTimerRef.current=intervalIdForOtpModalDialogTimer;
      }

    },(err)=>{
      parent_loadingModalDialogRef.current.close();
      parent_setIsOtpModalDialogToBeShown(false);
      parent_setIsTextDialogToBeShown(true);
      parent_setTextDialogText("Network error: Please check your network connection");
    });

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

      parent_loadingModalDialogRef.current.showModal();
      let otpVerificationDataObj={
        username: parent_getValuesRHF("username"),
        email: parent_getValuesRHF("email"),
        emailOtp: getOtpStringFromOtpCharInputsRef(emailOtpInputsRef),
        phoneNumberOtp: getOtpStringFromOtpCharInputsRef(phoneSmsOtpInputsRef)
      };

      let otpVerificationDataJson=JSON.stringify(otpVerificationDataObj);

      fetch("http://localhost:8080/auth/v1/forgot-password/verify-otps",{
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: otpVerificationDataJson
      })
      .then((response)=>{
        if(response.status===500) {
          throw new Error("500:Internal server error!");
        }

        if(response.ok) {
          return response.json();
        }

      },(err)=>{
        throw new Error("Network error: Please check your network connection");
      })
      .then((passwordResetTokenObj)=>{
        parent_loadingModalDialogRef.current.close();
        let passwordResetToken=passwordResetTokenObj["password reset token"];
        parent_passwordResetTokenRef.current=passwordResetToken;
        parent_setIsOtpModalDialogToBeShown(false);
        parent_setIsPasswordResetModalDialogToBeShown(true);
      },(err)=>{
        parent_loadingModalDialogRef.current.close();
        if(err.message==="500:Internal server error!") {
          parent_setIsOtpModalDialogToBeShown(false);
          parent_setIsTextDialogToBeShown(true);
          parent_setTextDialogText(err.message);
        } 
        else if(err.message==="Network error: Please check your network connection") {
          parent_setIsOtpModalDialogToBeShown(false);
          parent_setIsTextDialogToBeShown(true);
          parent_setTextDialogText(err.message);
        }
        else { /*json parsing error*/
          parent_setIsTextModalDialogToBeShown(true);
          parent_setTextModalDialogText("One or both of the entered otps are invalid!");
        }
      });

    }

return (
  <dialog ref={otpModalDialogRef} className={otpModalDialogStylesObj.otpDialog} closedby="closerequest" onClose={handleCloseForOtpModalDialog}>
    <div className={otpModalDialogStylesObj.otpdialogHeaderTextAndCloseButtonWrapper}>
      <span className={otpModalDialogStylesObj.otpDialogHeaderText}>OTP verification</span>
      <button className={otpModalDialogStylesObj.dialogCloseButton} onClick={handleClickForOtpDialogCloseButton}></button>
    </div>
    <form onSubmit={handleSubmitForOtpDialogForm}>
      <p>This website has been created for learning purposes and does not actually send otp to your email and phone no. The valid OTP is always <strong>100000</strong>.</p>            
      <p>If the entered details matched a user account in our system then separate otp's have been sent to the email and phone number associated with the account.</p>
      <p className={otpModalDialogStylesObj.emailOtpLabel}>Please enter the otp received through email:</p> 
      <div className={otpModalDialogStylesObj.otpEntriesWrapper}>
        {[0,1,2,3,4,5].map((index)=>
          (
            <input key={index} className={otpModalDialogStylesObj.otpCharEntry} maxLength="1"
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
      <p className={otpModalDialogStylesObj.phoneSmsOtpLabel}>Please enter the otp received through phone sms:</p>
      <div className={otpModalDialogStylesObj.otpEntriesWrapper}>
        {[0,1,2,3,4,5].map((index)=>
          (
            <input key={index} className={otpModalDialogStylesObj.otpCharEntry} maxLength="1"
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
      <div className={otpModalDialogStylesObj.otpTimeRemainingToExpireIndicator}>
        Both of the OTP's expire in {otpDialogTimeRemainingBeforeOtpExpires}
      </div>

      {otpDialogResendBothOtpButton}

      <div className={otpModalDialogStylesObj.verifyOtpAndCreateAccountButtonWrapper}>
        {otpDialogVerifyOtpButton}
      </div>
    </form>
  </dialog>
);
  
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

