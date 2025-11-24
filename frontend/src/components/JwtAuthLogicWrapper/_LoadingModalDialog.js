import * as React from "react";
import * as loadingModalDialogStylesObj from "./_LoadingModalDialog.css";


export default function LoadingModalDialog({ref}) {

  React.useEffect(()=>{
    document.body.classList.add(loadingModalDialogStylesObj.bodyBackgroundColorOverride);

    return ()=>{
      document.body.classList.remove(loadingModalDialogStylesObj.bodyBackgroundColorOverride);
    }
  },[]);

  return (
    <dialog ref={ref}  className={loadingModalDialogStylesObj.loginProcessingModalDialog} closedby="none">
      <div className={loadingModalDialogStylesObj.loadingSpinner}>
      </div>
    </dialog>
  );
}