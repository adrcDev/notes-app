import * as React from "react";
import * as editNotePageStylesObj from "./EditNotePage.css";
import { useLocation } from "react-router";
import AppBar from "./_AppBar.js";
import Quill from "quill";
import { JwtAccessTokenContext } from "../../contexts/JwtAcessTokenContext.js";
import { BackendUrlContext } from "../../contexts/BackendUrlContext.js";
import LoadingModalDialog from "./_LoadingModalDialog.js";
import TextDialog from "./_TextDialog.js";
import ModalRedirectDialog from "./_ModalRedirectDialog.js";
import { useNavigate } from "react-router";

export default function EditNotePage() {
  let navigateFuncReactRouter=useNavigate();
  let locationReactRouterObj=useLocation();
  let idOfNoteBeingEdited=locationReactRouterObj.state;

  let context_jwtAccessTokenRef=React.useContext(JwtAccessTokenContext);
  let context_backendUrl=React.useContext(BackendUrlContext);

  let [noteBeingEditedObj,setNoteBeingEditedObj]=React.useState(null);
  let [isTextDialogToBeShown,setIsTextDialogToBeShown]=React.useState(false);
  let [textDialogText,setTextDialogText]=React.useState("");
  let [isModalRedirectDialogToBeShown,setIsModalRedirectDialogToBeShown]=React.useState(false);
  let [modalRedirectDialogText,setModalRedirectDialogText]=React.useState("");
  

  let quillEditorContainerDivRef=React.useRef(null);
  let quillInstanceRef=React.useRef(null);
  let loadingModalDialogRef=React.useRef(null);
  let titleTextAreaRef=React.useRef(null);
  let descriptionTextAreaRef=React.useRef(null);
  let dueDateFieldRef=React.useRef(null);
  let priorityDropDownRef=React.useRef(null);
  let statusDropDownRef=React.useRef(null);

  let createdAtLocalTimeZoneTimeStamp;
  let updatedAtLocalTimeZoneTimeStamp; 
  if(noteBeingEditedObj!=null) {
    createdAtLocalTimeZoneTimeStamp=new Date(noteBeingEditedObj.createdAt).toString();
    updatedAtLocalTimeZoneTimeStamp=new Date(noteBeingEditedObj.updatedAt).toString();
  }

  React.useEffect(()=>{
      document.body.classList.add(editNotePageStylesObj.bodyBackgroundColorOverride);
  
      return ()=>{
        document.body.classList.remove(editNotePageStylesObj.bodyBackgroundColorOverride);
      }
    },[]);
    
  React.useEffect(()=>{
    async function getAndSetNote() {
      loadingModalDialogRef.current.showModal();
      let networkErrorMessage="Network error: please check your network connection";
      let somethingWentWrongMessage="Something went wrong, please try again";
      let response;
      try {
        response=await fetch(`${context_backendUrl}/api/v1/todos/${idOfNoteBeingEdited}`,{
          method: "get",
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
        console.log("500:Internal server error");
        setIsTextDialogToBeShown(true);
        setTextDialogText(somethingWentWrongMessage);
        return;
      }

      if(response.status===400) {
        loadingModalDialogRef.current.close();
        console.log("400:Bad request");
        setIsTextDialogToBeShown(true);
        setTextDialogText(somethingWentWrongMessage);
        return;
      }

      if(response.status===404) {
        loadingModalDialogRef.current.close();
        setIsModalRedirectDialogToBeShown(true);
        setModalRedirectDialogText("This note has been deleted. Redirecting to home page in 5 seconds");
        setTimeout(() => {
          navigateFuncReactRouter("/",{replace: true});
        }, 5000);
        return;
      }

      if(response.status===200) {
        loadingModalDialogRef.current.close();
        let noteBeingEditedParsedJsonObj=await response.json();
        setNoteBeingEditedObj(noteBeingEditedParsedJsonObj);
        return;
      }
      
      //response status for GET /api/v1/todos/{id} is 401
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
          response=await fetch(`${context_backendUrl}/api/v1/todos/${idOfNoteBeingEdited}`,{
          headers: {Authorization:`Bearer ${context_jwtAccessTokenRef.current}`},
          method: "get",
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
        console.log("500:Internal server error");
        setIsTextDialogToBeShown(true);
        setTextDialogText(somethingWentWrongMessage);
        return;
      }

      if(response.status===400) {
        loadingModalDialogRef.current.close();
        console.log("400:Bad request");
        setIsTextDialogToBeShown(true);
        setTextDialogText(somethingWentWrongMessage);
        return;
      }

      if(response.status===404) {
        loadingModalDialogRef.current.close();
        setIsModalRedirectDialogToBeShown(true);
        setModalRedirectDialogText("This note has been deleted. Redirecting to home page in 5 seconds");
        setTimeout(() => {
          navigateFuncReactRouter("/",{replace: true});
        }, 5000);
        return;
      }

      if(response.status===200) {
        loadingModalDialogRef.current.close();
        let noteBeingEditedParsedJsonObj=await response.json();
        setNoteBeingEditedObj(noteBeingEditedParsedJsonObj);
        return;
      }

      //response status for GET /api/v1/todos/{id} is 401
      loadingModalDialogRef.current.close();
      console.log("Unexpected 401 response");
      setIsTextDialogToBeShown(true);
      setTextDialogText(somethingWentWrongMessage);
    }

    getAndSetNote();
    },[context_backendUrl,context_jwtAccessTokenRef,idOfNoteBeingEdited,navigateFuncReactRouter]);

    React.useEffect(()=> {
      //prevents creation of 2 toolbars
      if(quillInstanceRef.current!=null) {
        return;
      }

      let quillEditorContainerDivDomNode=quillEditorContainerDivRef.current;
      let quillEditorConfig={
        theme: 'snow',
        modules: {
          toolbar: {
            container: [{ 'size': ['small', false, 'large', 'huge'] },{header:[1, 2, 3, 4, 5, 6, false]},{color:[]},{background:[]},"bold","italic",{align:[]},{ list: 'ordered'}, { list: 'bullet' }, { list: 'check' },"strike","underline",{ script: 'sub'}, { script: 'super' },"blockquote",{direction:"rtl"},{ indent: '-1'}, { indent: '+1' },"link","code","code-block","image","video"],
         // handlers: {
         //    "image": function (value) {
         //       console.log(value,this);
         //    }
         // }
          }
        }
      };
      let quillInstance=new Quill(quillEditorContainerDivDomNode,quillEditorConfig);
      quillInstanceRef.current=quillInstance;  
    },[]);

    React.useEffect(()=>{
      if(noteBeingEditedObj===null) {
        return;
      }

      if(noteBeingEditedObj.title!==null) {
        titleTextAreaRef.current.value=noteBeingEditedObj.title;
      }

      if(noteBeingEditedObj.description!==null) {
        descriptionTextAreaRef.current.value=noteBeingEditedObj.description;
      }

      if(noteBeingEditedObj.dueDate!==null) {
        dueDateFieldRef.current.value=noteBeingEditedObj.dueDate;
      }

      priorityDropDownRef.current.value=noteBeingEditedObj.priority;
      statusDropDownRef.current.value=noteBeingEditedObj.status;

      if(noteBeingEditedObj.contentDelta!==null) {
        let quillInstance=quillInstanceRef.current;
        let quillDeltaJsonString=noteBeingEditedObj.contentDelta;
        let quillDeltaObj=JSON.parse(quillDeltaJsonString);
        quillInstance.setContents(quillDeltaObj);
      }

    },[noteBeingEditedObj]);  

    function handleClickForClearDueDateFieldButton(e) {
      dueDateFieldRef.current.value="";
    }

    function handleClickForCloseWithoutSavingDialogCancelButton(e) {
      setIsTextDialogToBeShown(false);
      setTextDialogText("");
    }

    function handleClickForCloseWithoutSavingDialogYesButton(e) {
      navigateFuncReactRouter("/",{replace:true});
    }

    function handleClickForCloseWithoutSavingButton(e) {
      let dialogContentJsxObj=(
        <>
          <p>Are you sure you want to close this note without saving changes? (Any changes made since last save will be lost)</p>
          <div className={editNotePageStylesObj.closeWithoutSavingDialogButtonsWrapper}>
            <button className={editNotePageStylesObj.closeWithoutSavingDialogButton} onClick={handleClickForCloseWithoutSavingDialogYesButton}>Yes</button>
            <button className={editNotePageStylesObj.closeWithoutSavingDialogButton}
            onClick={handleClickForCloseWithoutSavingDialogCancelButton}
            >Cancel</button>
          </div>
        </>
      );
      setIsTextDialogToBeShown(true);
      setTextDialogText(dialogContentJsxObj);
    }


    return (
      <div className={editNotePageStylesObj.editNotePageWrapper}>
        <AppBar/>
        <div className={editNotePageStylesObj.noteActionButtonsWrapper}>
          <button className={editNotePageStylesObj.noteActionButton}>Delete</button>
          <button className={editNotePageStylesObj.noteActionButton}>Save</button>
          <button className={editNotePageStylesObj.noteActionButton}>Save and close</button>
          <button className={editNotePageStylesObj.noteActionButton}
            onClick={handleClickForCloseWithoutSavingButton}>Close without saving changes
          </button>
        </div>

        <div className={editNotePageStylesObj.timeStampsWrapper}>
          <label className={editNotePageStylesObj.timeStampLabel}>Created at:</label>
          <span>{createdAtLocalTimeZoneTimeStamp}</span>
          <label className={editNotePageStylesObj.timeStampLabel}>Updated at:</label>
          <span>{updatedAtLocalTimeZoneTimeStamp}</span>
        </div>

        <div className={editNotePageStylesObj.noteFieldsWrapper}>
          <label>Title:</label>
          <textarea ref={titleTextAreaRef} className={editNotePageStylesObj.textArea}></textarea>
          <label>Description:</label>
          <textarea ref={descriptionTextAreaRef} className={editNotePageStylesObj.textArea}></textarea>
          <label>Due date:</label>
          <div className={editNotePageStylesObj.dueDateFieldAndClearButtonWrapper}>
            <input ref={dueDateFieldRef} type="date" className={editNotePageStylesObj.dueDateField}></input>
            <button className={editNotePageStylesObj.clearDueDateFieldButton}
              onClick={handleClickForClearDueDateFieldButton}>Clear</button>
          </div>
          <label>Priority:</label>
          <select ref={priorityDropDownRef} className={`${editNotePageStylesObj.priorityDropDown} ${editNotePageStylesObj.dropDown}`}>
            <option>low</option>
            <option>medium</option>
            <option>high</option>
          </select>
          <label>Status:</label>
          <select ref={statusDropDownRef} className={`${editNotePageStylesObj.statusDropDown} ${editNotePageStylesObj.dropDown}`}>
            <option>not completed</option>
            <option>completed</option>
          </select>
        </div>

        <div className={editNotePageStylesObj.quillToolbarAndEditorWrapper}>
          <div ref={quillEditorContainerDivRef} className={editNotePageStylesObj.quillEditor}></div>
        </div>
        <LoadingModalDialog ref={loadingModalDialogRef} />
        {isTextDialogToBeShown && 
          <TextDialog text={textDialogText} parent_setIsTextDialogToBeShown={setIsTextDialogToBeShown} />}
        {isModalRedirectDialogToBeShown && 
          <ModalRedirectDialog text={modalRedirectDialogText} />}
      </div>
    );
}