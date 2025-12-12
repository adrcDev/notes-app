import * as homePageStylesObj from "./HomePage.css";
import * as React from "react";
import AppBar from "./_AppBar.js";
import FiltersAndSortBySetter from "./_FiltersAndSortBySetter.js";
import LoadingModalDialog from "./_LoadingModalDialog.js";
import TodosArea from "./_TodosArea.js";


export default function HomePage() {
  let [todosPageObj,setTodosPageObj]=React.useState(null);
  let [todosAreaSelectedPageNo,setTodosAreaSelectedPageNo]=React.useState(0);

  let loadingModalDialogRef=React.useRef(null);
  let filterAndSortUrlSearchParamsObjRef=React.useRef(null);

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
        parent_setTodosPageObj={setTodosPageObj}
        parent_filterAndSortUrlSearchParamsObjRef={filterAndSortUrlSearchParamsObjRef}
        parent_setTodosAreaSelectedPageNo={setTodosAreaSelectedPageNo}/>
      {todosPageObj!==null && 
      <TodosArea parent_todosPageObj={todosPageObj}
        parent_setTodosPageObj={setTodosPageObj}
        parent_loadingModalDialogRef={loadingModalDialogRef}
        parent_filterAndSortUrlSearchParamsObjRef={filterAndSortUrlSearchParamsObjRef}
        parent_todosAreaSelectedPageNo={todosAreaSelectedPageNo}
        parent_setTodosAreaSelectedPageNo={setTodosAreaSelectedPageNo}/>}
      <button className={homePageStylesObj.createNoteButton}>+</button>
      <LoadingModalDialog ref={loadingModalDialogRef} />
    </>
  );
}