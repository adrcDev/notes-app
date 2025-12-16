import * as React from "react";
import * as editNotePageStylesObj from "./EditNotePage.css";
import { useLocation } from "react-router";
import AppBar from "./_AppBar.js";
import Quill from "quill";

export default function EditNotePage() {
  let locationReactRouterObj=useLocation();
  let idOfNoteBeingEdited=locationReactRouterObj.state;
  let [noteBeingEditedObj,setNoteBeingEditedObj]=React.useState({});

  let quillEditorContainerDivRef=React.useRef(null);
  let quillInstanceRef=React.useRef(null);

  React.useEffect(()=>{
      document.body.classList.add(editNotePageStylesObj.bodyBackgroundColorOverride);
  
      return ()=>{
        document.body.classList.remove(editNotePageStylesObj.bodyBackgroundColorOverride);
      }
    },[]);
    
  React.useEffect(()=>{
    // getAndSetTodoBeingEdited();

    },[]);

    //temporarily added for testing quill editor
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
      const delta = {
  ops: [
    {
      insert: "Yellow ",
      attributes: { color: "yellow" }
    },
    {
      insert: "Blue ",
      attributes: { color: "blue" }
    },
    {
      insert: "Normal"
    }
  ]
};
      quillInstance.setContents(delta)
  
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

        <div className={editNotePageStylesObj.quillToolbarAndEditorWrapper}>
          <div ref={quillEditorContainerDivRef} className={editNotePageStylesObj.quillEditor}></div>
        </div>
      </div>
    );
}