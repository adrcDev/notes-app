import * as homePageStylesObj from "./HomePage.css";
import * as React from "react";
import AppBar from "./_AppBar.js";
import FiltersAndSortBySetter from "./_FiltersAndSortBySetter.js";
import LoadingModalDialog from "./_LoadingModalDialog.js";
import TodosArea from "./_TodosArea.js";
import TextDialog from "./_TextDialog.js";
import { useNavigate } from "react-router";
import { JwtAccessTokenContext } from "../../contexts/JwtAcessTokenContext.js";
import { BackendUrlContext } from "../../contexts/BackendUrlContext.js";

export default function HomePage() {
  let context_jwtAccessTokenRef=React.useContext(JwtAccessTokenContext);
  let context_backendUrl=React.useContext(BackendUrlContext);

  let [todosPageObj,setTodosPageObj]=React.useState(null);
  let [todosAreaSelectedPageNo,setTodosAreaSelectedPageNo]=React.useState(0);
  let [isTextDialogToBeShown,setIsTextDialogToBeShown]=React.useState(false);
  let [textDialogText,setTextDialogText]=React.useState("");

  let loadingModalDialogRef=React.useRef(null);
  let filterAndSortUrlSearchParamsObjRef=React.useRef(null);
  let navigateFuncReactRouter=useNavigate();

  React.useEffect(()=>{
    document.body.classList.add(homePageStylesObj.bodyBackgroundColorOverride);

    return ()=>{
      document.body.classList.remove(homePageStylesObj.bodyBackgroundColorOverride);
    }
  },[]);

  function handleClickForCreateNoteButton(e) {
    createNewNoteAndNavigateToEditNotePage();
  }

  async function createNewNoteAndNavigateToEditNotePage() {
      loadingModalDialogRef.current.showModal();
      let networkErrorMessage="Network error: please check your network connection";
      let somethingWentWrongMessage="Something went wrong please try again";
      let response;
      try {
      response=await fetch(`${context_backendUrl}/api/v1/todos`,{
        method: "post",
        headers: {
          "Authorization": `Bearer ${context_jwtAccessTokenRef.current}`
        },
        credentials: "include"
      });
    } catch(e) {
      loadingModalDialogRef.current.close();
      setIsTextDialogToBeShown(true);
      setTextDialogText(networkErrorMessage);
      return;
    }

    if(response.status===500) {
      loadingModalDialogRef.current.close();
      setIsTextDialogToBeShown(true);
      setTextDialogText(somethingWentWrongMessage);
      console.log("500: Internal server error");
      return;
    }

    if(response.status===201) {
      let createdNoteParsedJsonObj=await response.json();
      navigateFuncReactRouter("/edit-note",{replace:true,state:createdNoteParsedJsonObj});
      return;
    }

    //response status is 401 for POST /api/v1/todos 
    try {
      response=await fetch(`${context_backendUrl}/auth/v1/refresh`,{
        method: "post",
        credentials: "include"
      });
    } catch(e) {
      loadingModalDialogRef.current.close();
      setIsTextDialogToBeShown(true);
      setTextDialogText(networkErrorMessage);
      return;
    }

    if(response.status===500) {
      loadingModalDialogRef.current.close();
      setIsTextDialogToBeShown(true);
      setTextDialogText(somethingWentWrongMessage)
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
      response=await fetch(`${context_backendUrl}/api/v1/todos`,{
        method: "post",
        headers: {
          "Authorization": `Bearer ${context_jwtAccessTokenRef.current}`
        }
      });
    } catch(e) {
      loadingModalDialogRef.current.close();
      setIsTextDialogToBeShown(true);
      setTextDialogText(networkErrorMessage);
      return;
    }

    if(response.status===500) {
      loadingModalDialogRef.current.close();
      setIsTextDialogToBeShown(true);
      setTextDialogText(somethingWentWrongMessage)
      console.log("500: Internal server error");
      return;
    }

    if(response.status===401) {
      loadingModalDialogRef.current.close();
      setIsTextDialogToBeShown(true);
      setTextDialogText(somethingWentWrongMessage);
      console.log("Unexpected 401 unauthorized response received");
      return;
    }

    if(response.status===201) {
      let createdNoteParsedJsonObj=await response.json();
      navigateFuncReactRouter("/edit-note",{replace:true,state:createdNoteParsedJsonObj});
      return;
    }

  }


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
      <button className={homePageStylesObj.createNoteButton} 
        onClick={handleClickForCreateNoteButton}>+</button>
      {isTextDialogToBeShown && 
       <TextDialog text={textDialogText} parent_setIsTextDialogToBeShown={setIsTextDialogToBeShown} />}
      <LoadingModalDialog ref={loadingModalDialogRef} />
    </>
  );
}