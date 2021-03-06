package com.cs371group2.admin;

import com.cs371group2.account.Account;

/**
 * This object serves as a verifier class for accounts with functionality for checking
 * permissions, and should be extended for more specific verification requirements.
 *
 * History properties: This class is immutable from the time it is created.
 *
 * Invariance properties: This class makes no assumptions.
 * Created on 2017-02-09.
 */
public class PermissionVerifier {

    /**
     * Checks if the given account is verified.
     *
     * @param account The account to check for permissions.
     * @param token The AccessToken representing the user's information.
     * @return Whether the account has been verified or not.
     * @precond account != null and its fields are non-null.
     * @precond token != null and its fields are non-null.
     */
    public boolean hasPermission(Account account, AccessToken token){
        return true;
    }
}
