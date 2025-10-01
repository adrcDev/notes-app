import * as React from "react";
import * as textDialogStylesObj from "./_TextDialog.css";

export default function TextDialog({text,parent_setIsTextDialogToBeShown}) {
  let textDialogRef=React.useRef(null);

  React.useEffect(()=>{
    let textDialogDomNode=textDialogRef.current;
    textDialogDomNode.show();
  },[]);
  
  function handleClickForTextDialogCloseButton(e) {
    let textDialogDomNode=textDialogRef.current;
    textDialogDomNode.close();
  }

  function handleCloseForTextDialog(e) {
    parent_setIsTextDialogToBeShown(false);
  }

  return (
    <dialog ref={textDialogRef} className={textDialogStylesObj.textDialog} closedby="any" onClose={handleCloseForTextDialog}>
      <div className={textDialogStylesObj.dialogCloseButtonWrapper}>
          <button className={textDialogStylesObj.dialogCloseButton} onClick={handleClickForTextDialogCloseButton}></button>
      </div> 
      <p className={textDialogStylesObj.dialogText}>{text}</p>
    </dialog>
  );
  
}
