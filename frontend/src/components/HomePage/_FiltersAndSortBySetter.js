import * as filterAndSortBySetterStylesObj from "./_FiltersAndSortBySetter.css";
import * as React from "react";
import TextDialog from "./_TextDialog.js";
import { JwtAccessTokenContext } from "../../contexts/JwtAcessTokenContext.js";
import { useNavigate } from "react-router";
import { BackendUrlContext } from "../../contexts/BackendUrlContext.js";

export default function FiltersAndSortBySetter({parent_loadingModalDialogRef,parent_setTodosPageObj,parent_filterAndSortUrlSearchParamsObjRef,parent_setTodosAreaSelectedPageNo}) {
  let context_jwtAccessTokenRef=React.useContext(JwtAccessTokenContext);
  let context_backendUrl=React.useContext(BackendUrlContext);

  let navigateFuncReactRouter=useNavigate();

  let [selectedDueDateFilterRadioButtonLabelText,setSelectedDueDateFilterRadioButtonLabelText]=
    React.useState("all");
  let [selectedSortByRadioButtonLabelText,setSelectedSortByRadioButtonLabelText]=React.useState("due date");
  let [isTextDialogToBeShown,setIsTextDialogToBeShown]=React.useState(false);
  let [textDialogText,setTextDialogText]=React.useState("");

  let titleFilterTextFieldRef=React.useRef(null);
  let descriptionFilterTextFieldRef=React.useRef(null);
  let contentFilterTextFieldRef=React.useRef(null);
  let priortyFilterDropDownRef=React.useRef(null);
  let statusFilterDropDownRef=React.useRef(null);
  let sortOrderDropDownRef=React.useRef(null);
  let dueDateOnDateFieldRef=React.useRef(null);
  let dueDateBeforeDateFieldRef=React.useRef(null);
  let dueDateAfterDateFieldRef=React.useRef(null);
  let includeThisDateCheckBoxRef=React.useRef(null);
  let dueDateFromDateFieldRef=React.useRef(null);
  let dueDateToDateFieldRef=React.useRef(null);
  let dueDateFromIncludeDateCheckboxRef=React.useRef(null);
  let dueDateToIncludeDateCheckboxRef=React.useRef(null);
  
  let dueDateFilterDivJsxObj;
  if(selectedDueDateFilterRadioButtonLabelText==="on") {
    dueDateFilterDivJsxObj=(
      <div className={filterAndSortBySetterStylesObj.singleDueDateFilterWrapper}>
        <input type="date" ref={dueDateOnDateFieldRef}></input>
      </div>
    );
  }
  else if(selectedDueDateFilterRadioButtonLabelText==="before" || selectedDueDateFilterRadioButtonLabelText==="after") {
    dueDateFilterDivJsxObj=(
      <div className={filterAndSortBySetterStylesObj.singleDueDateFilterWrapper}>
        <div>
          <input type="date" ref={(domNode)=>{     
            if(selectedDueDateFilterRadioButtonLabelText==="before")
              dueDateBeforeDateFieldRef.current=domNode;
            else if(selectedDueDateFilterRadioButtonLabelText=="after")
              dueDateAfterDateFieldRef.current=domNode;
          }}></input>
          <div>
            <input type="checkbox" id="dueDateFilterIncludeThisDateCheckbox" className={filterAndSortBySetterStylesObj.dueDateFilterIncludeThisDateCheckbox} ref={includeThisDateCheckBoxRef}></input>
            <label htmlFor="dueDateFilterIncludeThisDateCheckbox" 
            className={filterAndSortBySetterStylesObj.dueDateFilterIncludeThisDateCheckboxLabel}>include this date</label>
          </div>
        </div>
      </div>
    );
  }
  else if(selectedDueDateFilterRadioButtonLabelText==="range") {
    dueDateFilterDivJsxObj=(
      <div className={filterAndSortBySetterStylesObj.doubleDueDateFilterWrapper}>
        <div>
            <input type="date" ref={dueDateFromDateFieldRef}></input>
            <div>
              <input type="checkbox" id="dueDateFilterIncludeThisDateCheckbox1" className={filterAndSortBySetterStylesObj.dueDateFilterIncludeThisDateCheckbox}
              ref={dueDateFromIncludeDateCheckboxRef}></input>  
              <label htmlFor="dueDateFilterIncludeThisDateCheckbox1" className={filterAndSortBySetterStylesObj.dueDateFilterIncludeThisDateCheckboxLabel}>include this date</label>
            </div>
        </div>
        <span>to</span>
        <div>
            <input type="date" ref={dueDateToDateFieldRef}></input>
            <div>
              <input type="checkbox" id="dueDateFilterIncludeThisDateCheckbox2" className={filterAndSortBySetterStylesObj.dueDateFilterIncludeThisDateCheckbox}
              ref={dueDateToIncludeDateCheckboxRef}></input>  
              <label htmlFor="dueDateFilterIncludeThisDateCheckbox2" className={filterAndSortBySetterStylesObj.dueDateFilterIncludeThisDateCheckboxLabel}>include this date</label>
            </div>
        </div>
      </div>
    );
  }
  
  function handleChangeForDueDateFilterRadioButton(e) {
    let clickedDueDateFilterRadioButtonValue=e.target.value;
    setSelectedDueDateFilterRadioButtonLabelText(clickedDueDateFilterRadioButtonValue);
  }

  function handleChangeForSortByRadioButton(e) {
    let clickedSortByRadioButtonValue=e.target.value;
    setSelectedSortByRadioButtonLabelText(clickedSortByRadioButtonValue);
  }

  function handleClickForResetFilterAndSortSettingsToDefaultButton(e) {
    titleFilterTextFieldRef.current.value="";
    descriptionFilterTextFieldRef.current.value="";
    contentFilterTextFieldRef.current.value="";
    priortyFilterDropDownRef.current.value="all";
    statusFilterDropDownRef.current.value="both";
    setSelectedDueDateFilterRadioButtonLabelText("all");
    setSelectedSortByRadioButtonLabelText("due date");
    sortOrderDropDownRef.current.value="ascending";
  }

  function handleClickForRetrieveTodosBasedOnSettingsButton(e) {
    // parent_loadingModalDialogRef.current.showModal();
    let urlQueryParamsObj=new URLSearchParams();
    if(titleFilterTextFieldRef.current.value.trim()!=="") {
      urlQueryParamsObj.append("titleSearch",titleFilterTextFieldRef.current.value.trim());
    }
    if(descriptionFilterTextFieldRef.current.value.trim()!=="") {
      urlQueryParamsObj.append("descriptionSearch",descriptionFilterTextFieldRef.current.value.trim());
    }
    if(contentFilterTextFieldRef.current.value.trim()!=="") {
      urlQueryParamsObj.append("contentSearch",contentFilterTextFieldRef.current.value.trim());
    }
    if(priortyFilterDropDownRef.current.value!=="all") {
      urlQueryParamsObj.append("priority",priortyFilterDropDownRef.current.value);
    }
    if(statusFilterDropDownRef.current.value!=="both") {
      urlQueryParamsObj.append("status",statusFilterDropDownRef.current.value);
    }

    if(selectedDueDateFilterRadioButtonLabelText==="on") {
      if(dueDateOnDateFieldRef.current.value==="") {
        setIsTextDialogToBeShown(true);
        setTextDialogText("Please choose a date from the date picker for setting the 'due date on' filter");
        return;
      }
      
      urlQueryParamsObj.append("dueDateFrom",dueDateOnDateFieldRef.current.value);
      urlQueryParamsObj.append("dueDateTo",dueDateOnDateFieldRef.current.value);
    }
    else if(selectedDueDateFilterRadioButtonLabelText==="before") {
      if(dueDateBeforeDateFieldRef.current.value==="") {
        setIsTextDialogToBeShown(true);
        setTextDialogText("Please choose a date from the date picker for setting the 'due date before' filter");
        return;
      }

      if(includeThisDateCheckBoxRef.current.checked) {
        urlQueryParamsObj.append("dueDateTo",dueDateBeforeDateFieldRef.current.value)
      }
      else {
        let dueDateBeforeString=dueDateBeforeDateFieldRef.current.value;
        let dueDateBeforeStringSubtractedBy1Day=subtract1DayFromDateString(dueDateBeforeString);
        urlQueryParamsObj.append("dueDateTo",dueDateBeforeStringSubtractedBy1Day);
      }
    }
    else if(selectedDueDateFilterRadioButtonLabelText==="after") {
      if(dueDateAfterDateFieldRef.current.value==="") {
        setIsTextDialogToBeShown(true);
        setTextDialogText("Please choose a date from the date picker for setting the 'due date after' filter");
        return;
      }

      if(includeThisDateCheckBoxRef.current.checked) {
        urlQueryParamsObj.append("dueDateFrom",dueDateAfterDateFieldRef.current.value)
      }
      else {
        let dueDateAfterString=dueDateAfterDateFieldRef.current.value;
        let dueDateAfterStringAddedToBy1Day=add1DayToDateString(dueDateAfterString);
        urlQueryParamsObj.append("dueDateFrom",dueDateAfterStringAddedToBy1Day);
      }
    }
    else if(selectedDueDateFilterRadioButtonLabelText==="range") {
      if(dueDateFromDateFieldRef.current.value==="") {
        setIsTextDialogToBeShown(true);
        setTextDialogText("Please choose a date from the date picker for setting the 'due date from' date picker field under the range due date filter");
        return;
      }
      if(dueDateToDateFieldRef.current.value==="") {
        setIsTextDialogToBeShown(true);
        setTextDialogText("Please choose a date from the date picker for setting the 'due date to' date picker field under the range due date filter");
        return;
      }

      let dueDateFromMsFromEpoch=Date.parse(`${dueDateFromDateFieldRef.current.value}T00:00:00Z`);
      let dueDateToMsFromEpoch=Date.parse(`${dueDateToDateFieldRef.current.value}T00:00:00Z`);
      if(dueDateFromMsFromEpoch>dueDateToMsFromEpoch) {
        setIsTextDialogToBeShown(true);
        setTextDialogText("The 'due date from' date picker field value cannot be greater than the 'due date to' date picker field value under the 'due date range' filter");
        return;
      }

      if(dueDateFromIncludeDateCheckboxRef.current.checked) {
        urlQueryParamsObj.append("dueDateFrom",dueDateFromDateFieldRef.current.value);
      }
      else {
        let dueDateFromDateString=dueDateFromDateFieldRef.current.value;
        let dueDateFromDateStringSubtractedBy1Day=subtract1DayFromDateString(dueDateFromDateString);
        urlQueryParamsObj.append("dueDateFrom",dueDateFromDateStringSubtractedBy1Day);
      }

      if(dueDateToIncludeDateCheckboxRef.current.checked) {
        urlQueryParamsObj.append("dueDateTo",dueDateToDateFieldRef.current.value);
      }
      else {
        let dueDateToDateString=dueDateToDateFieldRef.current.value;
        let dueDateToDateStringSubtractedBy1Day=subtract1DayFromDateString(dueDateToDateString);
        urlQueryParamsObj.append("dueDateTo",dueDateToDateStringSubtractedBy1Day);
      }
    }

    urlQueryParamsObj.append("sortBy",selectedSortByRadioButtonLabelText);
    urlQueryParamsObj.append("sortOrder",sortOrderDropDownRef.current.value);
    /* limit=10 and offset=0 are the default values of the GET api/v1/todos endpoint */
    urlQueryParamsObj.append("limit",10);
    urlQueryParamsObj.append("offset",0);
    let urlQueryParamsString=urlQueryParamsObj.toString();
    let jwtAccessToken= context_jwtAccessTokenRef.current;
    parent_loadingModalDialogRef.current.showModal();
    fetch(`${context_backendUrl}/api/v1/todos?${urlQueryParamsString}`,{
      headers: {Authorization:`Bearer ${jwtAccessToken}`},
      method: "get",
      credentials: "include"
    })
    .then((response)=>{
      if(response.status===400) {
        throw new Error("400:bad request");
      }

      if(response.status===500) {
        throw new Error("500:internal server error");
      }

      if(response.status===200) {
        return response.json();
      }

      if(response.status===401) {
        return fetch(`${context_backendUrl}/auth/v1/refresh`,{
          method: "post",
          credentials: "include"  //only added for development
        });  
      }

    })
    .then((obj)=>{
      let isObjAResponseObj=obj.status!==undefined;
      if(!isObjAResponseObj) {
        let todosPageJsonParsedObj=obj;
        // console.log(responseBodyJsonParsedObj);
        if(todosPageJsonParsedObj.totalTodos===0) {
          setIsTextDialogToBeShown(true);
          setTextDialogText("No notes were found that match the set filters");
          parent_setTodosPageObj(null);
          parent_setTodosAreaSelectedPageNo(0);
          parent_loadingModalDialogRef.current.close();
          return; //it is implicity return undefined
        }
        parent_filterAndSortUrlSearchParamsObjRef.current=urlQueryParamsObj;
        parent_setTodosPageObj(todosPageJsonParsedObj);
        parent_setTodosAreaSelectedPageNo(1);
        parent_loadingModalDialogRef.current.close();
      }
      else {
        let response=obj;
        if(response.status===500) {
          throw new Error("500:internal server error");
        } 
        
        if(response.status===401) {
          context_jwtAccessTokenRef.current="";
          navigateFuncReactRouter("/auth/login",{replace:true});
        }

        if(response.status===200) {
          return response.json();
        }
      }
      
    })
    .then((value)=>{
      let isResolvedValueAnObj=value!==undefined;
      if(isResolvedValueAnObj) {
        let jwtAccessTokenParsedJsonObj=value;
        let newJwtAccessToken=jwtAccessTokenParsedJsonObj["jwt access token"];
        context_jwtAccessTokenRef.current=newJwtAccessToken;
        return fetch(`${context_backendUrl}/api/v1/todos?${urlQueryParamsString}`,{
          headers: {Authorization:`Bearer ${newJwtAccessToken}`},
          method: "get",
          credentials: "include"
        });
      }
    })
    .then((value)=>{
      let isResolvedValueAResponseObj=value!==undefined;
      if(isResolvedValueAResponseObj) {
        let response=value;
        if(response.status===500) {
          throw new Error("500:internal server error")
        }

        if(response.status===401) {
          throw new Error("unexpected 401 error");
        }

        if(response.ok) {
          return response.json();
        }
      }
    })
    .then((value)=>{
      let isResolvedValueAnObj=value!==undefined;
      if(isResolvedValueAnObj) {
        let todosPageJsonParsedObj=value; 
        if(todosPageJsonParsedObj.totalTodos===0) {
          setIsTextDialogToBeShown(true);
          setTextDialogText("No todos were found that match the set filters");
          parent_setTodosPageObj(null);
          parent_setTodosAreaSelectedPageNo(0);
          parent_loadingModalDialogRef.current.close();
          return; //it is implicity return undefined
        }
        parent_filterAndSortUrlSearchParamsObjRef.current=urlQueryParamsObj;
        parent_setTodosPageObj(todosPageJsonParsedObj);
        parent_setTodosAreaSelectedPageNo(1);
        parent_loadingModalDialogRef.current.close();
      }
    })
    .catch((err)=>{
      parent_loadingModalDialogRef.current.close();
      if(err.message==="400:bad request") {
        console.error(err.message);
        setIsTextDialogToBeShown(true);
        setTextDialogText("Something went wrong please try again");
      }
      else if(err.message==="500:internal server error") {
        console.error(err.message);
        setIsTextDialogToBeShown(true);
        setTextDialogText("Something went wrong please try again");
      }
      else if(err.message==="unexpected 401 error") {
        console.error("unexpected 401 error!. Check whether the refresh endpoint's code is returning a new valid jwt access token");
        setIsTextDialogToBeShown(true);
        setTextDialogText("Something went wrong please try again");
      }
      else {
        setIsTextDialogToBeShown(true);
        setTextDialogText("Network error! Please check your network connection and try again");
      }
    });
    
  }


  return (
    <div className={filterAndSortBySetterStylesObj.filterAndSortBySetter}>
      <h2 className={filterAndSortBySetterStylesObj.filtersHeadingText}>Filters</h2>
      <div className={filterAndSortBySetterStylesObj.textBasedFiltersWrapper}>
        <label htmlFor="titleFilterTextField" className={filterAndSortBySetterStylesObj.textBasedFilterTextFieldLabel}>Title:</label>
        <input id="titleFilterTextField" className={filterAndSortBySetterStylesObj.textBasedFilterTextField} ref={titleFilterTextFieldRef}></input>

        <label htmlFor="descriptionFilterTextField" className={filterAndSortBySetterStylesObj.textBasedFilterTextFieldLabel}>Description:</label>
        <input id="descriptionFilterTextField" 
        className={filterAndSortBySetterStylesObj.textBasedFilterTextField}
        ref={descriptionFilterTextFieldRef}
        ></input>

        <label htmlFor="contentFilterTextField" className={filterAndSortBySetterStylesObj.textBasedFilterTextFieldLabel}>Content:</label>
        <input id="contentFilterTextField" 
        className={filterAndSortBySetterStylesObj.textBasedFilterTextField} ref={contentFilterTextFieldRef}></input>
      </div>
      <div className={filterAndSortBySetterStylesObj.textBasedFiltersUserHelperText}>You can leave any of the above text fields empty if you don't want to filter notes by the respective filter</div>
      
      <div className={filterAndSortBySetterStylesObj.priorityAndStatusFiltersDropDownWrapper}>
        <div className={filterAndSortBySetterStylesObj.labelAndFilterDropDownWrapper}>
          <label htmlFor="priorityFilterDropDown" className={filterAndSortBySetterStylesObj.filterDropDownLabel}>Priority</label>
          <select id="priorityFilterDropDown" className={filterAndSortBySetterStylesObj.filterDropDown} defaultValue="all" ref={priortyFilterDropDownRef}>
            <option>low</option>
            <option>medium</option>
            <option>high</option>
            <option>all</option>
          </select>
        </div>
        <div className={filterAndSortBySetterStylesObj.labelAndFilterDropDownWrapper}>
          <label htmlFor="statusFilterDropDown" className={filterAndSortBySetterStylesObj.filterDropDownLabel}>Status</label>
          <select id="statusFilterDropDown" className={filterAndSortBySetterStylesObj.filterDropDown} defaultValue="both" ref={statusFilterDropDownRef}>
            <option>not completed</option>
            <option>completed</option>
            <option>both</option>
          </select>
        </div>
      </div>

      <div className={filterAndSortBySetterStylesObj.dueDateFilterRadioButtonsWrapper}>
        <label>Due date:</label>
        <input type="radio" id="allDueDateFilterRadionButton" name="dueDate" value="all" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButton} checked={selectedDueDateFilterRadioButtonLabelText==="all"} onChange={handleChangeForDueDateFilterRadioButton}></input>
        <label htmlFor="allDueDateFilterRadionButton" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButtonLabel}>all</label>
        <input type="radio" id="onDueDateFilterRadionButton" name="dueDate" value="on" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButton} checked={selectedDueDateFilterRadioButtonLabelText==="on"} onChange={handleChangeForDueDateFilterRadioButton}></input>
        <label htmlFor="onDueDateFilterRadionButton" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButtonLabel}>on</label>
        <input type="radio" id="beforeDueDateFilterRadionButton" name="dueDate" value="before"
        checked={selectedDueDateFilterRadioButtonLabelText==="before"} className={filterAndSortBySetterStylesObj.dueDateFilterRadioButton} onChange={handleChangeForDueDateFilterRadioButton}></input>
        <label htmlFor="beforeDueDateFilterRadionButton" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButtonLabel}>before</label>
        <input type="radio" id="afterDueDateFilterRadionButton" name="dueDate" value="after"
          checked={selectedDueDateFilterRadioButtonLabelText==="after"} className={filterAndSortBySetterStylesObj.dueDateFilterRadioButton} onChange={handleChangeForDueDateFilterRadioButton}>
        </input>
        <label htmlFor="afterDueDateFilterRadionButton" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButtonLabel}>after</label>
        <input type="radio" id="rangeDueDateFilterRadionButton" name="dueDate" value="range"
          checked={selectedDueDateFilterRadioButtonLabelText==="range"} className={filterAndSortBySetterStylesObj.dueDateFilterRadioButton} onChange={handleChangeForDueDateFilterRadioButton}>
        </input>
        <label htmlFor="rangeDueDateFilterRadionButton" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButtonLabel}>range</label>
      </div>

      {dueDateFilterDivJsxObj}

      <div className={filterAndSortBySetterStylesObj.sortByWrapper}>
        <label>Sort by:</label>
        <div className={filterAndSortBySetterStylesObj.sortByRadioButtonsAndLabelsWrapper}>
          <div>
            <input type="radio" name="sortBy" value="due date" id="sortByDueDateRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButton} onChange={handleChangeForSortByRadioButton} checked={selectedSortByRadioButtonLabelText==="due date"}></input>
            <label htmlFor="sortByDueDateRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButtonLabel}>due date</label>
          </div>
          <div>
            <input type="radio" name="sortBy" value="priority" id="sortByPriorityRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButton} onChange={handleChangeForSortByRadioButton} checked={selectedSortByRadioButtonLabelText==="priority"}></input>
            <label htmlFor="sortByPriorityRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButtonLabel}>priority</label>
          </div>
          <div>
            <input type="radio" name="sortBy" value="creation date and time" id="sortByCreationDateAndTimeRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButton} onChange={handleChangeForSortByRadioButton} checked={selectedSortByRadioButtonLabelText==="creation date and time"}></input>
            <label htmlFor="sortByCreationDateAndTimeRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButtonLabel}>creation date and time</label>
          </div>
          <div>
            <input type="radio" name="sortBy" value="last updation date and time" id="sortByLastUpdationDateAndTimeRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButton} onChange={handleChangeForSortByRadioButton} checked={selectedSortByRadioButtonLabelText==="last updation date and time"}></input>
            <label htmlFor="sortByLastUpdationDateAndTimeRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButtonLabel}>last updation date and time</label>
          </div>
        </div>
      </div>

      <div className={filterAndSortBySetterStylesObj.sortOrderWrapper}>
        <label htmlFor="sortOrderDropDown">Sort order:</label>
        <select id="sortOrderDropDown" defaultValue="ascending" className={filterAndSortBySetterStylesObj.sortOrderDropDown} ref={sortOrderDropDownRef}>
          <option>ascending</option>
          <option>descending</option>
        </select>
      </div>

      <div className={filterAndSortBySetterStylesObj.retrieveTodosAndResetbuttonsWrapper}>
        <button className={filterAndSortBySetterStylesObj.actionButton} onClick={handleClickForRetrieveTodosBasedOnSettingsButton}>Retrieve notes based on filter and sort settings <span className={filterAndSortBySetterStylesObj.searchIconSpan}></span></button>
        <button className={filterAndSortBySetterStylesObj.actionButton} onClick={handleClickForResetFilterAndSortSettingsToDefaultButton}>Reset the filter and sort settings to defaults(For retrieving all notes)</button>
      </div>

      {isTextDialogToBeShown && 
        <TextDialog text={textDialogText} parent_setIsTextDialogToBeShown={setIsTextDialogToBeShown} />}
    </div>
  );
}

function subtract1DayFromDateString(dateString) {
  let dateObj=new Date(`${dateString}T00:00:00Z`);
  dateObj.setUTCDate(dateObj.getUTCDate()-1);      
  let year=dateObj.getUTCFullYear();
  let month=dateObj.getUTCMonth()+1;
  if(`${month}`.length==1) {
    month=`0${month}`;
  }
  let day=dateObj.getUTCDate();
  if(`${day}`.length==1) {
    day=`0${day}`;
  }
  let dateStringSubtractedBy1Day=`${year}-${month}-${day}`; 
  return dateStringSubtractedBy1Day;
}

function add1DayToDateString(dateString) {
  let dateObj=new Date(`${dateString}T00:00:00Z`);
  dateObj.setUTCDate(dateObj.getUTCDate()+1);      
  let year=dateObj.getUTCFullYear();
  let month=dateObj.getUTCMonth()+1;
  if(`${month}`.length==1) {
    month=`0${month}`;
  }
  let day=dateObj.getUTCDate();
  if(`${day}`.length==1) {
    day=`0${day}`;
  }
  let dateStringToWhich1DayAdded=`${year}-${month}-${day}`; 
  return dateStringToWhich1DayAdded;
}