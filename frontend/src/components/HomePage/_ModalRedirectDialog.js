import * as React from "react";
import * as modalRedirectDialogStylesObj from "./_ModalRedirectDialog.css";

export default function ModalRedirectDialog({text}) {
  let modalRedirectDialogRef=React.useRef(null);
  
    React.useEffect(()=>{
      let modalRedirectDialogDomNode=modalRedirectDialogRef.current;
      modalRedirectDialogDomNode.showModal();
    },[]);
    
    return (
      <dialog ref={modalRedirectDialogRef} className={modalRedirectDialogStylesObj.modalRedirectDialog} closedby="none">
        <div className={modalRedirectDialogStylesObj.dialogText}>{text}</div>
      </dialog>
    );

}