
import * as React from "react";
import * as ReactDOM from "react-dom/client";
import {createBrowserRouter,RouterProvider} from "react-router";
import "./global styles.css";
import ErrorPage from "./components/ErrorPage/ErrorPage.js";
import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";
import notesApiYamlFileUrl from "./static/yaml/notes-api.yaml";

let router=createBrowserRouter([
  {
    path:"/",
    element: <SwaggerUI url={notesApiYamlFileUrl} supportedSubmitMethods={[]}/>,
    errorElement: <ErrorPage/>
  }  
]);

const container = document.getElementById("root");
const root = ReactDOM.createRoot(container);
root.render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);




