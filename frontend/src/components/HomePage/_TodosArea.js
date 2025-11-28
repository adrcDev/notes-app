import * as React from "react";
import * as todosAreaStylesObj from "./_TodosArea.css";
import { JwtAccessTokenContext } from "../../contexts/JwtAcessTokenContext.js";
import { BackendUrlContext } from "../../contexts/BackendUrlContext.js";
import TextDialog from "./_TextDialog.js";
import { useNavigate } from "react-router";

export default function TodosArea({parent_todosPageObj,parent_setTodosPageObj,parent_loadingModalDialogRef,parent_filterAndSortUrlSearchParamsObjRef,parent_todosAreaSelectedPageNo,parent_setTodosAreaSelectedPageNo}) {
  let context_jwtAccessTokenRef=React.useContext(JwtAccessTokenContext);
  let context_backendUrl=React.useContext(BackendUrlContext);

  let navigateFuncReactRouter=useNavigate();

  let [textDialogText,setTextDialogText]=React.useState("");
  let [isTextDialogToBeShown,setIsTextDialogToBeShown]=React.useState(false);

  function handleClickForPageNoDiv(e) {
    let clickedPageNoDivDomNode=e.target;
    let clickedPageNo=Number.parseInt(clickedPageNoDivDomNode.textContent);
    fetchAndSetTodosPage(clickedPageNo);      
  }

  async function fetchAndSetTodosPage(pageNo) {
    parent_loadingModalDialogRef.current.showModal();
    let filterAndSortUrlSearchParamsObj=parent_filterAndSortUrlSearchParamsObjRef.current;
    filterAndSortUrlSearchParamsObj.delete("limit");
    filterAndSortUrlSearchParamsObj.delete("offset");
    filterAndSortUrlSearchParamsObj.append("limit",10);
    filterAndSortUrlSearchParamsObj.append("offset",(pageNo-1)*10);
    let jwtAccessToken= context_jwtAccessTokenRef.current;
    let response;
    try {
       response=await fetch(`${context_backendUrl}/api/v1/todos?${filterAndSortUrlSearchParamsObj.toString()}`,{
        headers: {Authorization:`Bearer ${jwtAccessToken}`},
        method: "get",
        credentials: "include" //only added for development
      });
    } catch(error) {
      setIsTextDialogToBeShown(true);
      setTextDialogText("Network error! Please check your network connection");
      parent_loadingModalDialogRef.current.close();
      return;
    }

    if(response.status===500 || response.status===400) {
      console.log(response.status,"response");
      setIsTextDialogToBeShown(true);
      setTextDialogText("Something went wrong please try again");
      parent_loadingModalDialogRef.current.close();
      return;
    }

    
    if(response.ok) {
      let todosPageJsonParsedObj=await response.json();
      if(todosPageJsonParsedObj.totalTodos===0) {
        parent_setTodosPageObj(null);
        parent_setTodosAreaSelectedPageNo(0);
        parent_loadingModalDialogRef.current.close();
        return;
      }

      let lastExistingPageNo=Math.ceil(todosPageJsonParsedObj.totalTodos/10);
      let isPageNoPassedAsArgumentValid=pageNo<=lastExistingPageNo;
      if(isPageNoPassedAsArgumentValid) {
        parent_setTodosPageObj(todosPageJsonParsedObj);
        parent_setTodosAreaSelectedPageNo(pageNo);
        parent_loadingModalDialogRef.current.close();
        return;
      }

      filterAndSortUrlSearchParamsObj.delete("offset");
      filterAndSortUrlSearchParamsObj.append("offset",(lastExistingPageNo-1)*10);
      try {
        response=await fetch(`${context_backendUrl}/auth/v1/todos?${filterAndSortUrlSearchParamsObj.toString()}`,{
          headers: {Authorization:`Bearer ${context_jwtAccessTokenRef.current}`},
          method: "get",
          credentials: "include"
        });
      } catch(error) {
        setIsTextDialogToBeShown(true);
        setTextDialogText("Network error! Please check your network connection");
        parent_loadingModalDialogRef.current.close();
        return;
      }

      if(response.status===500 || response.status===401 || response.status===400) {
        console.log(response.status,"response");
        setIsTextDialogToBeShown(true)
        setTextDialogText("Something went wrong please try again");
        parent_loadingModalDialogRef.current.close();
        return;
      }

      if(response.ok) {
        let todosLastPageJsonParsedObj=await response.json();
        parent_setTodosPageObj(todosLastPageJsonParsedObj);
        parent_setTodosAreaSelectedPageNo(lastExistingPageNo);
        parent_loadingModalDialogRef.current.close();
        return;
      }
    }

    if(response.status===401) {
      try {
        response=await fetch(`${context_backendUrl}/auth/v1/refresh`,{
          method: "post",
          credentials: "include" //added only for development
        });
      } catch(error) {
        setIsTextDialogToBeShown(true);
        setTextDialogText("Network error! Please check your network connection");
        parent_loadingModalDialogRef.current.close();
        return;
      }

      if(response.status===500) {
        console.log(response.status,"response");
        setIsTextDialogToBeShown(true)
        setTextDialogText("Something went wrong please try again");
        parent_loadingModalDialogRef.current.close();
        return;
      }

      if(response.status===401) {
        context_jwtAccessTokenRef.current="";
        navigateFuncReactRouter("/auth/login",{replace:true});
        return;
      }

      if(response.ok) {
        let jwtAccessTokenParsedJsonObj=await response.json();
        let newJwtAccessToken=jwtAccessTokenParsedJsonObj["jwt access token"];
        context_jwtAccessTokenRef.current=newJwtAccessToken;
        try {
          response=await fetch(`${context_backendUrl}/api/v1/todos?${filterAndSortUrlSearchParamsObj.toString()}`,{
          headers: {Authorization:`Bearer ${newJwtAccessToken}`},
          method: "get",
          credentials: "include" //only added for development
          });
        } catch(error) {
          setIsTextDialogToBeShown(true);
          setTextDialogText("Network error! Please check your network connection");
          parent_loadingModalDialogRef.current.close();
          return;
        }

        if(response.status===500 || response.status===401) {
          console.log(response.status,"response");
          setIsTextDialogToBeShown(true)
          setTextDialogText("Something went wrong please try again");
          parent_loadingModalDialogRef.current.close();
          return;
        }

        if(response.ok) {
          let todosPageJsonParsedObj=await response.json();
          if(todosPageJsonParsedObj.totalTodos===0) {
            parent_setTodosPageObj(null);
            parent_setTodosAreaSelectedPageNo(0);
            parent_loadingModalDialogRef.current.close();
            return;
          }

          let lastExistingPageNo=Math.ceil(todosPageJsonParsedObj.totalTodos/10);
          let isPageNoPassedAsArgumentValid=pageNo<=lastExistingPageNo;
          if(isPageNoPassedAsArgumentValid) {
            parent_setTodosPageObj(todosPageJsonParsedObj);
            parent_setTodosAreaSelectedPageNo(pageNo);
            parent_loadingModalDialogRef.current.close();
            return;
          }

          filterAndSortUrlSearchParamsObj.delete("offset");
          filterAndSortUrlSearchParamsObj.append("offset",(lastExistingPageNo-1)*10);
          try {
            response=await fetch(`${context_backendUrl}/auth/v1/todos?${filterAndSortUrlSearchParamsObj.toString()}`,{
              headers: {Authorization:`Bearer ${context_jwtAccessTokenRef.current}`},
              method: "get",
              credentials: "include"
            });
          } catch(error) {
            setIsTextDialogToBeShown(true);
            setTextDialogText("Network error! Please check your network connection");
            parent_loadingModalDialogRef.current.close();
            return;
          }

          if(response.status===500 || response.status===401) {
            console.log(response.status,"response");
            setIsTextDialogToBeShown(true)
            setTextDialogText("Something went wrong please try again");
            parent_loadingModalDialogRef.current.close();
            return;
          }

          if(response.ok) {
            let todosLastPageJsonParsedObj=await response.json();
            parent_setTodosPageObj(todosLastPageJsonParsedObj);
            parent_setTodosAreaSelectedPageNo(lastExistingPageNo);
            parent_loadingModalDialogRef.current.close();
            return;
          }

        }
      }
    }  
  }

  function handleClickForPreviousPageButton(e) {
    let currentSelectedPageNo=parent_todosAreaSelectedPageNo;
    let previousPageNo=currentSelectedPageNo-1;
    fetchAndSetTodosPage(previousPageNo);
  }

  function handleClickForNextPageButton(e) {
    let currentSelectedPageNo=parent_todosAreaSelectedPageNo;
    let nextPageNo=currentSelectedPageNo+1;
    fetchAndSetTodosPage(nextPageNo);
  }

  function getLastPageNo() {
    return Math.ceil(parent_todosPageObj.totalTodos/10);
  }


  
  let pageNoDivsJsxObjArr=[];
  let totalTodos=parent_todosPageObj.totalTodos;
  let noOfPages=Math.ceil(totalTodos/10);
  for(let pageNo=1;pageNo<=noOfPages;pageNo++) {
    let classNamesForPageNoDiv=todosAreaStylesObj.pageNoDiv;
    if(pageNo===parent_todosAreaSelectedPageNo) {
      classNamesForPageNoDiv=`${classNamesForPageNoDiv} ${todosAreaStylesObj.selectedPageNoDiv}`;
    }
    let pageNoDivJsxObj= (
      <div key={pageNo} className={classNamesForPageNoDiv} 
        onClick={handleClickForPageNoDiv}>{pageNo}</div>
    );
    pageNoDivsJsxObjArr.push(pageNoDivJsxObj)
  }
  let prevPageDivJsxObj=( 
    <button onClick={handleClickForPreviousPageButton} 
    disabled={parent_todosAreaSelectedPageNo===1}
    className={todosAreaStylesObj.prevOrNextPageButton}>Previous page</button>
  );
  let nextPageDivJsxObj=(
  <button onClick={handleClickForNextPageButton} 
  disabled={parent_todosAreaSelectedPageNo===getLastPageNo()}
  className={todosAreaStylesObj.prevOrNextPageButton}>Next page</button>
  );

  return (
    <>
      <div className={todosAreaStylesObj.todosArea}>
        <div className={todosAreaStylesObj.pageNoDivsWrapper}>
          {pageNoDivsJsxObjArr}
        </div>
        <div className={todosAreaStylesObj.prevNextPageDivsWrapper}>
          {prevPageDivJsxObj}
          {nextPageDivJsxObj}
        </div>
      </div>
      {isTextDialogToBeShown && <TextDialog text={textDialogText} 
        parent_setIsTextDialogToBeShown={setIsTextDialogToBeShown}/>}
    </>
  );
  
}