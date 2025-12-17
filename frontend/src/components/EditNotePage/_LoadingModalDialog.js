import * as React from "react";
import * as loadingModalDialogStylesObj from "./_LoadingModalDialog.css";


export default function LoadingModalDialog({ref}) {

  return (
    <dialog ref={ref}  className={loadingModalDialogStylesObj.loginProcessingModalDialog} closedby="none">
      <div className={loadingModalDialogStylesObj.loadingSpinner}>
      </div>
    </dialog>
  );
}