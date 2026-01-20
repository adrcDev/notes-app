import * as React from "react";
import * as textModalDialogStylesObj from "./_TextModalDialog.css";

export default function TextModalDialog({text}) {
  let textModalDialogRef=React.useRef(null);
  
    React.useEffect(()=>{
      let textModalDialogDomNode=textModalDialogRef.current;
      textModalDialogDomNode.showModal();
    },[]);
    
    return (
      <dialog ref={textModalDialogRef} className={textModalDialogStylesObj.modalRedirectDialog} closedby="none">
        <div className={textModalDialogStylesObj.dialogText}>{text}</div>
      </dialog>
    );

}