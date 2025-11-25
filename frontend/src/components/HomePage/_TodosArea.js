import * as React from "react";
import * as todosAreaStylesObj from "./_TodosArea.css";
import { JwtAccessTokenContext } from "../../contexts/JwtAcessTokenContext.js";
import { BackendUrlContext } from "../../contexts/BackendUrlContext.js";

export default function TodosArea({parent_todosPageObj,parent_setTodosPageObj,parent_loadingModalDialogRef}) {
  let context_jwtAccessTokenRef=React.useContext(JwtAccessTokenContext);
  let context_backendUrl=React.useContext(BackendUrlContext);
  let pageNoDivsJsxObjArr=[];
  let prevPageDiv;
  let nextPageDiv;

  if(parent_todosPageObj!==null) {
    let totalTodos=parent_todosPageObj.totalTodos;
    let noOfPages=Math.ceil(totalTodos/10);
    for(let pageNo=1;pageNo<=noOfPages;pageNo++) {
      let pageNoDivJsxObj=<div key={pageNo}>{pageNo}</div>
      pageNoDivsJsxObjArr.push(pageNoDivJsxObj)
    }
    prevPageDiv=<div>Previous page</div>;
    nextPageDiv=<div>Next page</div>
  }

  return (
    <div className={todosAreaStylesObj.todosArea}>
      <div className={todosAreaStylesObj.pageNoDivsWrapper}>
        {pageNoDivsJsxObjArr}
        {prevPageDiv}
        {nextPageDiv}
      </div>
    </div>
  );
}