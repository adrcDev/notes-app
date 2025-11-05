import * as homePageStylesObj from "./HomePage.css";
import * as React from "react";
import AppBar from "./_AppBar.js";
import FiltersAndSortBySetter from "./_FiltersAndSortBySetter.js";
import LoadingModalDialog from "./_LoadingModalDialog.js"

export default function HomePage() {
  let loadingModalDialogRef=React.useRef(null);

  return (
    <>
      <AppBar/>
      <FiltersAndSortBySetter parent_loadingModalDialogRef={loadingModalDialogRef}/>
      <LoadingModalDialog ref={loadingModalDialogRef} />
    </>
  );
}