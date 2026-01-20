import * as React from "react";
import * as loadingModalDialogStylesObj from "./_LoadingModalDialog.css";


export default function LoadingModalDialog() {
  let loadingModalDialogRef=React.useRef(null);

  React.useEffect(()=>{
    document.body.classList.add(loadingModalDialogStylesObj.bodyBackgroundColorOverride);

    return ()=>{
      document.body.classList.remove(loadingModalDialogStylesObj.bodyBackgroundColorOverride);
    }
  },[]);

  React.useEffect(()=>{
    loadingModalDialogRef.current.showModal();
  },[]);

  return (
    <dialog ref={loadingModalDialogRef}  className={loadingModalDialogStylesObj.loginProcessingModalDialog} closedby="none">
      <div className={loadingModalDialogStylesObj.loadingSpinner}>
      </div>
    </dialog>
  );
}