import * as React from "react";
import editNotePageStylesObj from "./EditNotePage.css";
import { useLocation } from "react-router";

export default function EditNotePage() {
  let locationReactRouterObj=useLocation();
  let [noteBeingEditedObj,setNoteBeingEditedObj]=React.useState(locationReactRouterObj.state);
}