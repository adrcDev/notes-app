
import * as React from "react";
import * as ReactDOM from "react-dom/client";
import {createBrowserRouter,RouterProvider} from "react-router";
/*import this polyfill library only after you have finished your developing your app/site. As if it is included then it will make the webpack compilation process take more time to complete*/
// import "core-js/actual";
import "./global styles.css";
import ErrorPage from "./components/ErrorPage/ErrorPage.js";

let router=createBrowserRouter([
  {
    path:"/",
    element: <HomePage/>,
    errorElement: <ErrorPage/>
  },
]);

const container = document.getElementById("root");
const root = ReactDOM.createRoot(container);
root.render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);

function HomePage() {
  return <h1>This is home page</h1>;
}