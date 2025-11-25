
import * as React from "react";
import * as ReactDOM from "react-dom/client";
import {createBrowserRouter,RouterProvider} from "react-router";
/*import this polyfill library only after you have finished your developing your app/site. As if it is included then it will make the webpack compilation process take more time to complete*/
// import "core-js/actual";
import "./global styles.css";
import ErrorPage from "./components/ErrorPage/ErrorPage.js";
import AuthPage from "./components/AuthPage/AuthPage.js";
import LoginForm from "./components/LoginForm/LoginForm.js";
import { ThemeProvider } from "./contexts/ThemeContext.js";
import { JwtAccessTokenProvider } from "./contexts/JwtAcessTokenContext.js";
import SignupForm from "./components/SignupForm/SignupForm.js";
import ForgotPasswordForm from "./components/ForgotPasswordForm/ForgotPasswordForm.js";
import JwtAuthLogicWrapper from "./components/JwtAuthLogicWrapper/JwtAuthLogicWrapper.js";
import HomePage from "./components/HomePage/HomePage.js";
import {BackendUrlProvider} from "./contexts/BackendUrlContext.js";

let router=createBrowserRouter([
  {
    path:"/",
    element: <JwtAuthLogicWrapper><HomePage/></JwtAuthLogicWrapper>,
    errorElement: <ErrorPage/>
  },
  {
    path: "/auth", /*TODO- might have to change this path segment name */
    element: <JwtAuthLogicWrapper><AuthPage /></JwtAuthLogicWrapper>,
    // element:<AuthPage/>,
    children: [
      {
        path: "login",
        element: <LoginForm />
      },
      {
        path: "signup",
        element: <SignupForm />
      },
      {
        path: "forgot-password",
        element: <ForgotPasswordForm/>
      }
    ] 
  },

]);

const container = document.getElementById("root");
const root = ReactDOM.createRoot(container);
root.render(
  <React.StrictMode>
    <ThemeProvider>
      <JwtAccessTokenProvider>
        <BackendUrlProvider>
          <RouterProvider router={router} />
        </BackendUrlProvider>
      </JwtAccessTokenProvider>
    </ThemeProvider>
  </React.StrictMode>
);

