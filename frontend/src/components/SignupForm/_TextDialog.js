import * as React from "react";
import * as textDialogStylesObj from "./_TextDialog.css";

export default function TextDialog({text}) {
  let textDialogRef=React.useRef(null);

  React.useEffect(()=>{
    let textDialogDomNode=textDialogRef.current;
    textDialogDomNode.show();

    return ()=>{
      textDialogDomNode.close();
    }
  },[]);
  
  function handleClickForTextDialogCloseButton(e) {
    let textDialogDomNode=textDialogRef.current;
    textDialogDomNode.close();
  }

  return (
    <dialog ref={textDialogRef} className={textDialogStylesObj.textDialog} closedby="any">
      <div className={textDialogStylesObj.dialogCloseButtonWrapper}>
          <button className={textDialogStylesObj.dialogCloseButton} onClick={handleClickForTextDialogCloseButton}></button>
      </div> 
      <p className={textDialogStylesObj.dialogText}>{text}</p>
    </dialog>
  );
  
}
