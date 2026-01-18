import "./App.scss";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import HomePage from "./pages/homepage/HomePage";
import AppHeader from "./components/appheader/AppHeader";

const router = createBrowserRouter([
  {
    path: "/",
    element: <HomePage />,
  },
]);

function App() {
  return (
    <>
      <AppHeader />
      <RouterProvider router={router} />
    </>
  );
}

export default App;
