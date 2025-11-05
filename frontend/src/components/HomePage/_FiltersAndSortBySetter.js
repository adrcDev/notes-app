import * as filterAndSortBySetterStylesObj from "./_FiltersAndSortBySetter.css";
import * as React from "react";
import TextDialog from "./_TextDialog.js";

export default function FiltersAndSortBySetter({parent_loadingModalDialogRef}) {
  let [selectedDueDateFilterRadioButton,setSelectedDueDateFilterRadioButton]=
    React.useState("all");
  let [selectedSortByRadioButton,setSelectedSortByRadioButton]=React.useState("due date");
  let [isTextDialogToBeShown,setIsTextDialogToBeShown]=React.useState(false);
  let [textDialogText,setTextDialogText]=React.useState("");

  let titleFilterTextFieldRef=React.useRef(null);
  let descriptionFilterTextFieldRef=React.useRef(null);
  let contentFilterTextFieldRef=React.useRef(null);
  let priortyFilterDropDownRef=React.useRef(null);
  let statusFilterDropDownRef=React.useRef(null);
  let sortOrderDropDownRef=React.useRef(null);
  
  
  let dueDateFilterDivJsxObj;
  if(selectedDueDateFilterRadioButton==="on") {
    dueDateFilterDivJsxObj=(
      <div className={filterAndSortBySetterStylesObj.singleDueDateFilterWrapper}>
        <input type="date"></input>
      </div>
    );
  }
  else if(selectedDueDateFilterRadioButton==="before" || selectedDueDateFilterRadioButton==="after") {
    dueDateFilterDivJsxObj=(
      <div className={filterAndSortBySetterStylesObj.singleDueDateFilterWrapper}>
        <div>
          <input type="date"></input>
          <div>
            <input type="checkbox" id="dueDateFilterIncludeThisDateCheckbox" className={filterAndSortBySetterStylesObj.dueDateFilterIncludeThisDateCheckbox}></input>
            <label htmlFor="dueDateFilterIncludeThisDateCheckbox" 
            className={filterAndSortBySetterStylesObj.dueDateFilterIncludeThisDateCheckboxLabel}>include this date</label>
          </div>
        </div>
      </div>
    );
  }
  else if(selectedDueDateFilterRadioButton==="range") {
    dueDateFilterDivJsxObj=(
      <div className={filterAndSortBySetterStylesObj.doubleDueDateFilterWrapper}>
        <div>
            <input type="date"></input>
            <div>
              <input type="checkbox" id="dueDateFilterIncludeThisDateCheckbox1" className={filterAndSortBySetterStylesObj.dueDateFilterIncludeThisDateCheckbox}></input>  
              <label htmlFor="dueDateFilterIncludeThisDateCheckbox1" className={filterAndSortBySetterStylesObj.dueDateFilterIncludeThisDateCheckboxLabel}>include this date</label>
            </div>
        </div>
        <span>to</span>
        <div>
            <input type="date"></input>
            <div>
              <input type="checkbox" id="dueDateFilterIncludeThisDateCheckbox2" className={filterAndSortBySetterStylesObj.dueDateFilterIncludeThisDateCheckbox}></input>  
              <label htmlFor="dueDateFilterIncludeThisDateCheckbox2" className={filterAndSortBySetterStylesObj.dueDateFilterIncludeThisDateCheckboxLabel}>include this date</label>
            </div>
        </div>
      </div>
    );
  }
  
  function handleChangeForDueDateFilterRadioButton(e) {
    let clickedDueDateFilterRadioButtonValue=e.target.value;
    setSelectedDueDateFilterRadioButton(clickedDueDateFilterRadioButtonValue);
  }

  function handleChangeForSortByRadioButton(e) {
    let clickedSortByRadioButtonValue=e.target.value;
    setSelectedSortByRadioButton(clickedSortByRadioButtonValue);
  }

  function handleClickForResetFilterAndSortSettingsToDefaultButton(e) {
    titleFilterTextFieldRef.current.value="";
    descriptionFilterTextFieldRef.current.value="";
    contentFilterTextFieldRef.current.value="";
    priortyFilterDropDownRef.current.value="all";
    statusFilterDropDownRef.current.value="both";
    setSelectedDueDateFilterRadioButton("all");
    setSelectedSortByRadioButton("due date");
    sortOrderDropDownRef.current.value="ascending";
  }

  function handleClickForRetrieveTodosBasedOnSettingsButton(e) {
    // parent_loadingModalDialogRef.current.showModal();
    // setIsTextDialogToBeShown(true);
    // setTextDialogText("hi there!");
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
      <div className={filterAndSortBySetterStylesObj.textBasedFiltersUserHelperText}>You can leave any of the above text fields empty if you don't want to filter todo's by the respective filter</div>
      
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
        <input type="radio" id="allDueDateFilterRadionButton" name="dueDate" value="all" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButton} checked={selectedDueDateFilterRadioButton==="all"} onChange={handleChangeForDueDateFilterRadioButton}></input>
        <label htmlFor="allDueDateFilterRadionButton" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButtonLabel}>all</label>
        <input type="radio" id="onDueDateFilterRadionButton" name="dueDate" value="on" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButton} checked={selectedDueDateFilterRadioButton==="on"} onChange={handleChangeForDueDateFilterRadioButton}></input>
        <label htmlFor="onDueDateFilterRadionButton" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButtonLabel}>on</label>
        <input type="radio" id="beforeDueDateFilterRadionButton" name="dueDate" value="before"
        checked={selectedDueDateFilterRadioButton==="before"} className={filterAndSortBySetterStylesObj.dueDateFilterRadioButton} onChange={handleChangeForDueDateFilterRadioButton}></input>
        <label htmlFor="beforeDueDateFilterRadionButton" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButtonLabel}>before</label>
        <input type="radio" id="afterDueDateFilterRadionButton" name="dueDate" value="after"
          checked={selectedDueDateFilterRadioButton==="after"} className={filterAndSortBySetterStylesObj.dueDateFilterRadioButton} onChange={handleChangeForDueDateFilterRadioButton}>
        </input>
        <label htmlFor="afterDueDateFilterRadionButton" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButtonLabel}>after</label>
        <input type="radio" id="rangeDueDateFilterRadionButton" name="dueDate" value="range"
          checked={selectedDueDateFilterRadioButton==="range"} className={filterAndSortBySetterStylesObj.dueDateFilterRadioButton} onChange={handleChangeForDueDateFilterRadioButton}>
        </input>
        <label htmlFor="rangeDueDateFilterRadionButton" className={filterAndSortBySetterStylesObj.dueDateFilterRadioButtonLabel}>range</label>
      </div>

      {dueDateFilterDivJsxObj}

      <div className={filterAndSortBySetterStylesObj.sortByWrapper}>
        <label>Sort by:</label>
        <div className={filterAndSortBySetterStylesObj.sortByRadioButtonsAndLabelsWrapper}>
          <div>
            <input type="radio" name="sortBy" value="due date" id="sortByDueDateRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButton} onChange={handleChangeForSortByRadioButton} checked={selectedSortByRadioButton==="due date"}></input>
            <label htmlFor="sortByDueDateRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButtonLabel}>due date</label>
          </div>
          <div>
            <input type="radio" name="sortBy" value="priority" id="sortByPriorityRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButton} onChange={handleChangeForSortByRadioButton} checked={selectedSortByRadioButton==="priority"}></input>
            <label htmlFor="sortByPriorityRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButtonLabel}>priority</label>
          </div>
          <div>
            <input type="radio" name="sortBy" value="creation date and time" id="sortByCreationDateAndTimeRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButton} onChange={handleChangeForSortByRadioButton} checked={selectedSortByRadioButton==="creation date and time"}></input>
            <label htmlFor="sortByCreationDateAndTimeRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButtonLabel}>creation date and time</label>
          </div>
          <div>
            <input type="radio" name="sortBy" value="last updation date and time" id="sortByLastUpdationDateAndTimeRadioButton" className={filterAndSortBySetterStylesObj.sortByRadioButton} onChange={handleChangeForSortByRadioButton} checked={selectedSortByRadioButton==="last updation date and time"}></input>
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
        <button className={filterAndSortBySetterStylesObj.actionButton} onClick={handleClickForRetrieveTodosBasedOnSettingsButton}>Retrieve todo's based on filter and sort settings <span className={filterAndSortBySetterStylesObj.searchIconSpan}></span></button>
        <button className={filterAndSortBySetterStylesObj.actionButton} onClick={handleClickForResetFilterAndSortSettingsToDefaultButton}>Reset the filter and sort settings to defaults(For retrieving all todos)</button>
      </div>

      {isTextDialogToBeShown && 
        <TextDialog text={textDialogText} parent_setIsTextDialogToBeShown={setIsTextDialogToBeShown} />}
    </div>
  );
}