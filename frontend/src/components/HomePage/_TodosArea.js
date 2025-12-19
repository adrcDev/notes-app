import * as React from "react";
import * as todosAreaStylesObj from "./_TodosArea.css";
import { JwtAccessTokenContext } from "../../contexts/JwtAcessTokenContext.js";
import { BackendUrlContext } from "../../contexts/BackendUrlContext.js";
import TextDialog from "./_TextDialog.js";
import { useNavigate } from "react-router";
import Quill from "quill";
import ModalRedirectDialog from "./_ModalRedirectDialog.js";

export default function TodosArea({parent_todosPageObj,parent_setTodosPageObj,parent_loadingModalDialogRef,parent_filterAndSortUrlSearchParamsObjRef,parent_todosAreaSelectedPageNo,parent_setTodosAreaSelectedPageNo}) {
  let context_jwtAccessTokenRef=React.useContext(JwtAccessTokenContext);
  let context_backendUrl=React.useContext(BackendUrlContext);

  let navigateFuncReactRouter=useNavigate();

  let [textDialogText,setTextDialogText]=React.useState("");
  let [isTextDialogToBeShown,setIsTextDialogToBeShown]=React.useState(false);
  let [isModalRedirectDialogToBeShown,setIsModalRedirectDialogToBeShown]=React.useState(false);
  let [modalRedirectDialogText,setModalRedirectDialogText]=React.useState("");
  
  let readOnlyQuillEditorContainerDivsArrRef=React.useRef([]);
  let todoTitleTextFieldArrRef=React.useRef([]);

  React.useEffect(()=>{
    let readOnlyQuillEditorContainerDivsArr=readOnlyQuillEditorContainerDivsArrRef.current;
    for(let i=0;i<readOnlyQuillEditorContainerDivsArr.length;i++) { 
      if(readOnlyQuillEditorContainerDivsArr[i]===null) {
        continue;
      }
      let containerDiv=readOnlyQuillEditorContainerDivsArr[i];
      let readOnlyQuillEditorConfig={
        theme: "snow",
        readOnly: true,
        modules: {
          toolbar: null
        }
      };
      let readOnlyQuillEditor=new Quill(containerDiv,readOnlyQuillEditorConfig);
      let todoObj=parent_todosPageObj.todos[i];
      let todoContentDeltaJson=todoObj.contentDelta;
      let todoContentDeltaObj=JSON.parse(todoContentDeltaJson)
      readOnlyQuillEditor.setContents(todoContentDeltaObj);
    }

    let todoTitleTextFieldArr=todoTitleTextFieldArrRef.current;
    for(let i=0;i<todoTitleTextFieldArr.length;i++) {
      if(todoTitleTextFieldArr[i]===null) {
        continue;
      }
      let titleTextFieldDomNode=todoTitleTextFieldArr[i];
      let todoObj=parent_todosPageObj.todos[i];
      let todoTitle=todoObj.title;
      if(todoTitle===null || todoTitle.trim()==="") {
        titleTextFieldDomNode.value="Untitled";
      } else {
        titleTextFieldDomNode.value=todoTitle;
      }
    }
    
  },[parent_todosPageObj.todos]);

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
        response=await fetch(`${context_backendUrl}/api/v1/todos?${filterAndSortUrlSearchParamsObj.toString()}`,{
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
            response=await fetch(`${context_backendUrl}/api/v1/todos?${filterAndSortUrlSearchParamsObj.toString()}`,{
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

  function handleClickForEditTodoSpan(e,todoId) {
    parent_loadingModalDialogRef.current.showModal();
    navigateFuncReactRouter("/edit-note",{replace:true,state:todoId});
  }

  function handleClickForTodoInfoSpan(e,todoId) {
    let todosDisplayedOnPageArr=parent_todosPageObj.todos;
    let clickedTodoObj=todosDisplayedOnPageArr.find((todoObj,index)=>todoObj.id===todoId);
    let todoInfoDivsJsxObjArr=[];
    let keyValueForJsxListElements=0;
    let todoInfoDialogContentDivJsxObj=(
      <div className={todosAreaStylesObj.todoInfoDialogContentWrapper}>
        <div>Note info:-</div>
        {todoInfoDivsJsxObjArr}
      </div>
    );
    
    
    let shortTodoDescription="<Not set>";
    if(clickedTodoObj.description!==null && clickedTodoObj.description.trim()!=="") {
      shortTodoDescription=clickedTodoObj.description;
      if(shortTodoDescription.length>100) {
        shortTodoDescription=`${shortTodoDescription.substring(0,100)}...`;
      }
    }
    let shortTodoDescriptionDivJsxObj=(
        <div key={keyValueForJsxListElements++}>Description: {shortTodoDescription}</div>
    );
    todoInfoDivsJsxObjArr.push(shortTodoDescriptionDivJsxObj);

    let dueDate="<Not set>";
    if(clickedTodoObj.dueDate!==null) {
      let dueDateAsDateObj=new Date(clickedTodoObj.dueDate);
      let day=dueDateAsDateObj.getUTCDate();
      let shortDayName=getShortDayName(dueDateAsDateObj.getUTCDay());
      let month=dueDateAsDateObj.getUTCMonth()+1;
      let shortMonthName=getShortMonthName(month);
      let year=dueDateAsDateObj.getUTCFullYear();
      dueDate=`${shortDayName} ${shortMonthName} ${day} ${year}`;
    }
    let dueDateDivJsxObj=(
      <div key={keyValueForJsxListElements++}>Due date: {dueDate}</div>
    );
    todoInfoDivsJsxObjArr.push(dueDateDivJsxObj);


    let priorityDivJsxObj=(
      <div key={keyValueForJsxListElements++}>Priority: {clickedTodoObj.priority}</div>
    );
    todoInfoDivsJsxObjArr.push(priorityDivJsxObj);
    
    let statusDivJsxObj=(
      <div key={keyValueForJsxListElements++}>Status: {clickedTodoObj.status}</div>
    );
    todoInfoDivsJsxObjArr.push(statusDivJsxObj);
    
    let createdAtUTCTimestamp=clickedTodoObj.createdAt;
    let createdAtLocalTimeZoneTimeStamp=new Date(createdAtUTCTimestamp).toString();
    let createdAtDivJsxObj=(
      <div key={keyValueForJsxListElements++}>Created at: {createdAtLocalTimeZoneTimeStamp}</div>
    );
    todoInfoDivsJsxObjArr.push(createdAtDivJsxObj);

    let updatedAtUTCTimestamp=clickedTodoObj.updatedAt;
    let updatedAtLocalTimeZoneTimeStamp=new Date(updatedAtUTCTimestamp).toString();
    let updatedAtDivJsxObj=(
      <div key={keyValueForJsxListElements++}>Updated at: {updatedAtLocalTimeZoneTimeStamp}</div>
    );
    todoInfoDivsJsxObjArr.push(updatedAtDivJsxObj);

    setIsTextDialogToBeShown(true);
    setTextDialogText(todoInfoDialogContentDivJsxObj);
  }

  function handleClickForDeleteTodoDialogCancelButton(e) {
    setIsTextDialogToBeShown(false)
    setTextDialogText("");
  }

  function handleClickForDeleteTodoDialogYesButton(e,todoId) {
    deleteTodoAndResetPage(todoId);
  }

  function handleClickForDeleteTodoSpan(e,todoId) {
    let dialogContentJsxObj=(
      <div>
        <p>Are you sure you want to delete the note?</p>
        <div className={todosAreaStylesObj.deleteTodoDialogButtonsWrapper}>
          <button className={todosAreaStylesObj.deleteTodoDialogButton}
            onClick={(e)=>handleClickForDeleteTodoDialogYesButton(e,todoId)}>Yes</button>
          <button className={todosAreaStylesObj.deleteTodoDialogButton} 
            onClick={handleClickForDeleteTodoDialogCancelButton}>Cancel</button>
        </div>
      </div>
    );
    setIsTextDialogToBeShown(true);
    setTextDialogText(dialogContentJsxObj);
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
  let readOnlyQuillEditorContainerDivJsxObjsArr=[];
  let todosToBeDisplayedInPageArr=parent_todosPageObj.todos;
  for(let i=0;i<todosToBeDisplayedInPageArr.length;i++) {
    let todoId=todosToBeDisplayedInPageArr[i].id;
    let readOnlyQuillEditorContainerDivJsxObj=(
      <div key={todoId} className={todosAreaStylesObj.todoWrapper}>
        <div className={todosAreaStylesObj.todoTitleTextFieldAndLabelWrapper}>
          <label className={todosAreaStylesObj.todoTitleLabel}>Title:</label>
          <input readOnly={true} className={todosAreaStylesObj.todoTitleTextField}
          ref={(domNode)=>{
            todoTitleTextFieldArrRef.current[i]=domNode;
          }}></input>
        </div>
        <div  ref={(domNode)=>{
          readOnlyQuillEditorContainerDivsArrRef.current[i]=domNode;
        }} className={`${todosAreaStylesObj.todoReadOnlyQuillEditor} ${todosAreaStylesObj.tempTodoReadOnlyQuillEditor}`}></div>
        <div className={todosAreaStylesObj.todoActionsWrapper}>
          <span className={`${todosAreaStylesObj.todoActionSpan} ${todosAreaStylesObj.editTodoSpan}`} onClick={(e)=>handleClickForEditTodoSpan(e,todoId)}></span>
          <span className={`${todosAreaStylesObj.todoActionSpan} ${todosAreaStylesObj.deleteTodoSpan}`} onClick={(e)=>handleClickForDeleteTodoSpan(e,todoId)}></span>
          <span className={`${todosAreaStylesObj.todoActionSpan} ${todosAreaStylesObj.todoInfoSpan}`} onClick={(e)=>handleClickForTodoInfoSpan(e,todoId)}></span>
        </div>
       </div>
    );
    readOnlyQuillEditorContainerDivJsxObjsArr.push(readOnlyQuillEditorContainerDivJsxObj);
  }

  async function deleteTodoAndResetPage(todoId) {
      parent_loadingModalDialogRef.current.showModal();
      let networkErrorMessage="Network error: please check your network connection";
      let somethingWentWrongMessage="Something went wrong: please try again";
      let response;
      try {
        response=await fetch(`${context_backendUrl}/api/v1/todos/${todoId}`,{
          method: "delete",
          headers: {
            "Authorization": `Bearer ${context_jwtAccessTokenRef.current}`
          },
          credentials: "include"
        });
      } catch(e) {
        parent_loadingModalDialogRef.current.close();
        setIsTextDialogToBeShown(true);
        setTextDialogText(networkErrorMessage);
        return;
      }

      if(response.status===500) {
        parent_loadingModalDialogRef.current.close();
        console.log("500: Internal server error");
        setIsTextDialogToBeShown(true);
        setTextDialogText(somethingWentWrongMessage);
        return;
      }

      if(response.status===400) {
        parent_loadingModalDialogRef.current.close();
        console.log("400: Bad request");
        setIsTextDialogToBeShown(true);
        setTextDialogText(somethingWentWrongMessage);
        return;
      }

      if(response.status===404) {
        parent_loadingModalDialogRef.current.close();
        setIsTextDialogToBeShown(false);
        setTextDialogText("");
        setIsModalRedirectDialogToBeShown(true);
        setModalRedirectDialogText("Couldn't delete this note as it has already been deleted. Resetting page in 3 seconds");
        setTimeout(() => {
          setIsModalRedirectDialogToBeShown(false);
          setModalRedirectDialogText("");
          fetchAndSetTodosPage(parent_todosAreaSelectedPageNo);
        }, 3000);
        return;
      }

      if(response.status===204) {
        parent_loadingModalDialogRef.current.close();
        setIsTextDialogToBeShown(false);
        setTextDialogText("");
        fetchAndSetTodosPage(parent_todosAreaSelectedPageNo);
        return;
      }

      //response status code is 401 for DELETE /api/v1/todos/{id}
      try {
        response=await fetch(`${context_backendUrl}/auth/v1/refresh`,{
          method: "post",
          credentials: "include" 
        });
      } catch(e) {
        parent_loadingModalDialogRef.current.close();
        setIsTextDialogToBeShown(true);
        setTextDialogText(networkErrorMessage);
        return;
      }

      if(response.status===500) {
        parent_loadingModalDialogRef.current.close();
        setIsTextDialogToBeShown(true)
        setTextDialogText(somethingWentWrongMessage);
        console.log("500: Internal server error");
        return;
      }

      if(response.status===401) {
        context_jwtAccessTokenRef.current="";
        navigateFuncReactRouter("/auth/login",{replace:true});
        return;
      }

      //response status is 200 for /auth/v1/refresh
      let jwtAccessTokenParsedJsonObj=await response.json();
      let newJwtAccessToken=jwtAccessTokenParsedJsonObj["jwt access token"];
      context_jwtAccessTokenRef.current=newJwtAccessToken;

      try {
        response=await fetch(`${context_backendUrl}/api/v1/todos/${todoId}`,{
          method: "delete",
          headers: {
            "Authorization": `Bearer ${context_jwtAccessTokenRef.current}`
          },
          credentials: "include"
        });
      } catch(e) {
        parent_loadingModalDialogRef.current.close();
        setIsTextDialogToBeShown(true);
        setTextDialogText(networkErrorMessage);
        return;
      }

      if(response.status===500) {
        parent_loadingModalDialogRef.current.close();
        console.log("500: Internal server error");
        setIsTextDialogToBeShown(true);
        setTextDialogText(somethingWentWrongMessage);
        return;
      }

      if(response.status===400) {
        parent_loadingModalDialogRef.current.close();
        console.log("400: Bad request");
        setIsTextDialogToBeShown(true);
        setTextDialogText(somethingWentWrongMessage);
        return;
      }

      if(response.status===404) {
        parent_loadingModalDialogRef.current.close();
        setIsTextDialogToBeShown(false);
        setTextDialogText("");
        setIsModalRedirectDialogToBeShown(true);
        setModalRedirectDialogText("Couldn't delete this note as it has already been deleted. Refreshing page in 3 seconds");
        setTimeout(() => {
          setIsModalRedirectDialogToBeShown(false);
          setModalRedirectDialogText("");
          fetchAndSetTodosPage(parent_todosAreaSelectedPageNo);
        }, 3000);
        return;
      }

      if(response.status===204) {
        parent_loadingModalDialogRef.current.close();
        setIsTextDialogToBeShown(false);
        setTextDialogText("");
        fetchAndSetTodosPage(parent_todosAreaSelectedPageNo);
        return;
      }

      //response status code is 401 for DELETE /api/v1/todos/{id}
      parent_loadingModalDialogRef.current.close();
      console.log("Unexpected 401 response");
      setIsTextDialogToBeShown(true);
      setTextDialogText(somethingWentWrongMessage);
      return;
    }


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
        <div className={todosAreaStylesObj.todosWrapper}>
            {readOnlyQuillEditorContainerDivJsxObjsArr}
        </div>
      </div>
      {isTextDialogToBeShown && 
        <TextDialog text={textDialogText} 
         parent_setIsTextDialogToBeShown={setIsTextDialogToBeShown}/>}
      {isModalRedirectDialogToBeShown && 
        <ModalRedirectDialog text={modalRedirectDialogText} />}
    </>
  );
  
}

function getShortDayName(dayNo) {
  if(dayNo===0)
    return "Sun";
  if(dayNo===1)
    return "Mon";
  if(dayNo===2)
    return "Tue";
  if(dayNo===3)
    return "Wed";
  if(dayNo===4)
    return "Thu";
  if(dayNo===5)
    return "Fri";
  if(dayNo===6)
    return "Sat";
}

function getShortMonthName(monthNo) {
  if(monthNo===1)
    return "Jan";
  if(monthNo===2)
    return "Feb";
  if(monthNo===3)
    return "Mar";
  if(monthNo===4)
    return "Apr";
  if(monthNo===5)
    return "May";
  if(monthNo===6)
    return "Jun";
  if(monthNo===7)
    return "Jul";
  if(monthNo===8)
    return "Aug";
  if(monthNo===9)
    return "Sep";
  if(monthNo===10)
    return "Oct";
  if(monthNo===11)
    return "Nov";
  if(monthNo===12)
    return "Dec";
}