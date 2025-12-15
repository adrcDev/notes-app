import * as React from "react";
import * as editNotePageStylesObj from "./EditNotePage.css";
import { useLocation } from "react-router";
import AppBar from "./_AppBar.js";

export default function EditNotePage() {
  let locationReactRouterObj=useLocation();
  let idOfNoteBeingEdited=locationReactRouterObj.state;
  let [noteBeingEditedObj,setNoteBeingEditedObj]=React.useState({});

  React.useEffect(()=>{
      document.body.classList.add(editNotePageStylesObj.bodyBackgroundColorOverride);
  
      return ()=>{
        document.body.classList.remove(editNotePageStylesObj.bodyBackgroundColorOverride);
      }
    },[]);
    
  React.useEffect(()=>{
    // getAndSetTodoBeingEdited();

    },[]);



    return (
      <div className={editNotePageStylesObj.editNotePageWrapper}>
        <AppBar/>
        <div className={editNotePageStylesObj.noteActionButtonsWrapper}>
          <button className={editNotePageStylesObj.noteActionButton}>Delete</button>
          <button className={editNotePageStylesObj.noteActionButton}>Save</button>
          <button className={editNotePageStylesObj.noteActionButton}>Save and close</button>
          <button className={editNotePageStylesObj.noteActionButton}>Close without saving changes</button>
        </div>

        <div className={editNotePageStylesObj.timeStampsWrapper}>
          <label className={editNotePageStylesObj.timeStampLabel}>Created at:</label>
          {/*placeholder*/}
          <span>Sun Jun 22 2000 13:23:32.234543(+5:30GMT) indian standard time</span>
          <label className={editNotePageStylesObj.timeStampLabel}>Updated at:</label>
          {/*placeholder*/}
          <span>Sun Jun 22 2000 13:23:32.234543(+5:30GMT) indian standard time</span>
        </div>

        <div className={editNotePageStylesObj.noteFieldsWrapper}>
          <label>Title:</label>
          <textarea className={editNotePageStylesObj.textArea}></textarea>
          <label>Description:</label>
          <textarea className={editNotePageStylesObj.textArea}></textarea>
          <label>Due date:</label>
          <div className={editNotePageStylesObj.dueDateFieldAndClearButtonWrapper}>
            <input type="date" className={editNotePageStylesObj.dueDateField}></input>
            <button className={editNotePageStylesObj.clearDueDateFieldButton}>Clear</button>
          </div>
          <label>Priority:</label>
          <select className={`${editNotePageStylesObj.priorityDropDown} ${editNotePageStylesObj.dropDown}`}>
            <option>low</option>
            <option>medium</option>
            <option>high</option>
          </select>
          <label>Status:</label>
          <select className={`${editNotePageStylesObj.statusDropDown} ${editNotePageStylesObj.dropDown}`}>
            <option>not completed</option>
            <option>completed</option>
          </select>
        </div>

      </div>
    );
}