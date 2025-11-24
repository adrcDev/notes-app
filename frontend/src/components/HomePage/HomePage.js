import * as homePageStylesObj from "./HomePage.css";
import * as React from "react";
import AppBar from "./_AppBar.js";
import FiltersAndSortBySetter from "./_FiltersAndSortBySetter.js";
import LoadingModalDialog from "./_LoadingModalDialog.js";
import { ThemeContext } from "../../contexts/ThemeContext.js";


export default function HomePage() {
  let loadingModalDialogRef=React.useRef(null);
  let [todosPageObj,setTodosPageObj]=React.useState(null);


  React.useEffect(()=>{
    document.body.classList.add(homePageStylesObj.bodyBackgroundColorOverride);

    return ()=>{
      document.body.classList.remove(homePageStylesObj.bodyBackgroundColorOverride);
    }
  },[]);

  return (
    <>
      <AppBar/>
      <FiltersAndSortBySetter parent_loadingModalDialogRef={loadingModalDialogRef}
        parent_setTodosPageObj={setTodosPageObj}/>
      <LoadingModalDialog ref={loadingModalDialogRef} />
    </>
  );
}