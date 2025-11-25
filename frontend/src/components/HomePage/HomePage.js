import * as homePageStylesObj from "./HomePage.css";
import * as React from "react";
import AppBar from "./_AppBar.js";
import FiltersAndSortBySetter from "./_FiltersAndSortBySetter.js";
import LoadingModalDialog from "./_LoadingModalDialog.js";
import TodosArea from "./_TodosArea.js";


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
      <TodosArea parent_todosPageObj={todosPageObj}
        parent_setTodosPageObj={setTodosPageObj}
        parent_loadingModalDialogRef={loadingModalDialogRef}/>
      <LoadingModalDialog ref={loadingModalDialogRef} />
    </>
  );
}