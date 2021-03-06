/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2017-02-15 17:18:02 UTC)
 * on 2017-03-18 at 22:39:45 UTC 
 * Modify at your own risk.
 */

package com.appspot.ltc_safety.admin.model;

/**
 * Model definition for Account.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the Admin API. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Account extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String email;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean emailVerified;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String permissions;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean testingAccount;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEmail() {
    return email;
  }

  /**
   * @param email email or {@code null} for none
   */
  public Account setEmail(java.lang.String email) {
    this.email = email;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getEmailVerified() {
    return emailVerified;
  }

  /**
   * @param emailVerified emailVerified or {@code null} for none
   */
  public Account setEmailVerified(java.lang.Boolean emailVerified) {
    this.emailVerified = emailVerified;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public Account setId(java.lang.String id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPermissions() {
    return permissions;
  }

  /**
   * @param permissions permissions or {@code null} for none
   */
  public Account setPermissions(java.lang.String permissions) {
    this.permissions = permissions;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getTestingAccount() {
    return testingAccount;
  }

  /**
   * @param testingAccount testingAccount or {@code null} for none
   */
  public Account setTestingAccount(java.lang.Boolean testingAccount) {
    this.testingAccount = testingAccount;
    return this;
  }

  @Override
  public Account set(String fieldName, Object value) {
    return (Account) super.set(fieldName, value);
  }

  @Override
  public Account clone() {
    return (Account) super.clone();
  }

}
