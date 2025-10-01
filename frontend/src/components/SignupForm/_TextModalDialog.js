import * as React from "react";
import * as textModalDialogStylesObj from "./_TextModalDialog.css";

export default function TextModalDialog({text,parent_setIsTextModalDialogToBeShown}) {
  let textModalDialogRef=React.useRef(null);

  React.useEffect(()=>{
    let textModalDialogDomNode=textModalDialogRef.current;
    textModalDialogDomNode.showModal();
  },[]);
  
  function handleClickForTextModalDialogCloseButton(e) {
    let textModalDialogDomNode=textModalDialogRef.current;
    textModalDialogDomNode.close();
  }

  function handleCloseForTextModalDialog(e) {
    parent_setIsTextModalDialogToBeShown(false);
  }

  return (
    /* Not exactly a modal dialog due to the closedby="any" property but needed a modal dialog in order to appear over the signup otp verification modal dialog*/
    <dialog ref={textModalDialogRef} className={textModalDialogStylesObj.textModalDialog} closedby="any" onClose={handleCloseForTextModalDialog}>
      <div className={textModalDialogStylesObj.dialogCloseButtonWrapper}>
          <button className={textModalDialogStylesObj.dialogCloseButton} onClick={handleClickForTextModalDialogCloseButton}></button>
      </div> 
      <p className={textModalDialogStylesObj.dialogText}>{text}</p>
    </dialog>
  );
  
}
