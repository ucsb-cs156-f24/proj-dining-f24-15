import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "main/pages/HomePage";
import ProfilePage from "main/pages/ProfilePage";
import AdminUsersPage from "main/pages/AdminUsersPage";

import MenuItemPage from "main/pages/MenuItem/MenuItemPage";
import DiningCommonsPage from "main/pages/DiningCommons/DiningCommonsPage";

import { hasRole, useCurrentUser } from "main/utils/currentUser";

import "bootstrap/dist/css/bootstrap.css";
import "react-toastify/dist/ReactToastify.css";

function App() {
  const { data: currentUser } = useCurrentUser();

  return (
    <BrowserRouter>
      <Routes>
        <Route exact path="/" element={<HomePage />} />
        <Route exact path="/profile" element={<ProfilePage />} />
        {hasRole(currentUser, "ROLE_ADMIN") && (
          <Route exact path="/admin/users" element={<AdminUsersPage />} />
        )}
        {hasRole(currentUser, "ROLE_USER") && (
          <>
            <Route
              exact
              path="/diningcommons/:date-time/:dining-commons-code/:meal"
              element={<MenuItemPage />}
            />
          </>
        )}
        <>
          <Route
            exact
            path="/diningcommons/:diningCommonsCode"
            element={<DiningCommonsPage />}
          />
        </>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
