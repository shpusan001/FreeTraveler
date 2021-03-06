import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import styled from "styled-components";
import { changeAccount, getAccount } from "../../module/account";
import { FormControl, TextField, NativeSelect } from "@mui/material";
import Button from "../../component/post/buttons/DayButton";
import palette from "../../lib/styles/palette";

const ProfileTemplate = styled.div`
  width: auto;
  /* height: 100%; */
  padding: 35px;
  margin-top: -10px;
  margin-left: 10%;
  margin-right: 10%;
  background-color: white;
  min-height: 100vh;
`;
const ProfileForm = styled.form`
  margin-left: 30%;
  margin-right: 30%;
  margin-top: 5%;
  @media screen and (max-width: 1024px) {
    margin-left: 0;
    margin-right: 0;
  }
`;

const ProfileInputTitle = styled.form`
  text-align: left;
  margin-top: 1rem;
  /* margin-left: 0.5rem; */
  margin-bottom: 0.5rem;
  font-weight: bold;
  font-size: 0.9rem;
  width: 100%;
  @media screen and (max-width: 612px) {
    font-size: 0.7rem;
  }
`;
const ProfileTitle = styled.div`
  /* margin-left: 10px; */
  font-size: 18px;
  font-weight: 700;
  color: ${palette.gray[8]};
`;
const ProfileInput = styled.input`
  border-radius: 2px;
  width: 100%;
  height: 40px;
  border: 1px solid #e5e5e5;
  padding: 9px 12px;
  outline: none;
  box-sizing: border-box;
  &:focus {
    color: $oc-teal-7;
    border-bottom: 1px solid ${palette.gray[7]};
  }

  & + & {
    margin-top: 1rem;
  }
`;
const ProfileButton = styled.button`
  width: 100%;
  height: 40px;
  font-size: 20px;
  padding: 13px 30px;
  cursor: pointer;
  background-color: black;
  color: white;
  line-height: 1px;
  margin-top: 20px;
  margin-bottom: 12px;
  border-radius: 3px;
  border-style: none;
  :hover {
    background-color: ${palette.gray[9]};
  }
`;

const ErrorMesageBox = styled.div`
  color: red;
  margin-bottom: 20px;
  font-size: 10pt;
`;

export default function AccountContainer() {
  let [accountId, setAccountId] = useState("");
  let [accountName, setAccountName] = useState("");
  let [accountOldPassword, setAccountOldPassword] = useState("");
  let [accountNewPassword, setAccountNewPassword] = useState("");
  let [accountNewPasswordConfirm, setAccountNewPasswordConfirm] = useState("");

  let [nameIntegrity, setNameIntegrity] = useState(null);
  let [oldPasswordIntegrity, setOldPasswordIntegrity] = useState(null);
  let [newPasswordIntegrity, setNewPasswordIntegrity] = useState(null);
  let [newPasswordConfirmIntegrity, setNewPasswordConfirmIntegrity] =
    useState(null);

  let [passing, setPassing] = useState([]);

  const { data } = useSelector((account) => ({
    data: account.account.account,
  }));
  const dispatch = useDispatch();

  //????????? ?????? ??????
  useEffect(() => {
    dispatch(getAccount());
  }, []);

  //????????? ?????? ?????????
  useEffect(() => {
    const accountIdInput = document.getElementById("account_id");
    const accountNameInput = document.getElementById("account_name");
    // const accountOldPasswordInput = document.getElementById(
    //   "account_old_password"
    // );
    // const accountNewPasswordInput = document.getElementById(
    //   "accout_new_password"
    // );
    // const accountNewPasswordConfirmInput = document.getElementById(
    //   "account_new_password_confirm"
    // );
    console.log(data);
    if (data != null) {
      // accountIdInput.value = data.id;
      // accountNameInput.value = data.name;
      setAccountId(data.id);
      setAccountName(data.name);
    }
  }, [data]);

  useEffect(() => {
    {
      let pass = false;
      const regex = /^([???-???]{2,4}|[a-zA-Z]{2,16})$/;
      pass = regex.test(accountName);
      if (!pass) {
        setNameIntegrity(
          "2????????? 4????????? ????????????, 2????????? 16????????? ????????? ??????????????????."
        );
      } else {
        setNameIntegrity("");
      }
      passing[0] = pass;
    }

    {
      let pass = false;
      const regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*\W).{8,20}$/;
      pass = regex.test(accountOldPassword);
      if (!pass) {
        setOldPasswordIntegrity(
          "?????????, ??????, ??????????????? ???????????? 8????????? 20??? ????????? ??????????????????."
        );
      } else {
        setOldPasswordIntegrity("");
      }
      passing[1] = pass;
    }

    {
      let pass = false;
      const regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*\W).{8,20}$/;
      pass = regex.test(accountNewPassword);
      if (!pass) {
        setNewPasswordIntegrity(
          "?????????, ??????, ??????????????? ???????????? 8????????? 20??? ????????? ??????????????????."
        );
      } else {
        setNewPasswordIntegrity("");
      }
      passing[2] = pass;
    }

    //???????????? ?????? ??????
    {
      let pass = false;
      pass = accountNewPassword == accountNewPasswordConfirm;
      if (!pass) {
        setNewPasswordConfirmIntegrity("??????????????? ?????? ?????? ????????????.");
      } else {
        setNewPasswordConfirmIntegrity("");
      }
      passing[3] = pass;
    }

    setPassing(passing);
  }, [
    accountName,
    accountOldPassword,
    accountNewPassword,
    accountNewPasswordConfirm,
  ]);

  const onChange = (e) => {
    const { name, value } = e.target;
    switch (name) {
      case "account_id":
        setAccountId(value);
        break;
      case "account_name":
        setAccountName(value);
        break;
      case "account_old_password":
        setAccountOldPassword(value);
        break;
      case "account_new_password":
        setAccountNewPassword(value);
        break;
      case "account_new_password_confirm":
        setAccountNewPasswordConfirm(value);
        break;
    }
  };

  const onSubmit = (e) => {
    e.preventDefault();
    const temp = {
      userId: accountId,
      name: accountName,
      oldPassword: accountOldPassword,
      password: accountNewPassword,
      passwordConfirm: accountNewPasswordConfirm,
    };
    dispatch(changeAccount(temp));
  };

  return (
    <ProfileTemplate>
      <ProfileForm onSubmit={onSubmit}>
        <ProfileTitle>????????? ??????</ProfileTitle>
        <ProfileInputTitle>????????? </ProfileInputTitle>{" "}
        <ProfileInput
          fullWidth
          id="accunt_id"
          name="account_id"
          onChan={onChange}
          value={accountId}
          // variant="standard"
          disabl
        />{" "}
        <ProfileInputTitle>?????? </ProfileInputTitle>
        <ProfileInput
          fullWidth
          id="account_name"
          name="account_name"
          onChange={onChange}
          value={accountName}
          // variant="standard"
        />{" "}
        <br />
        {/* ????????? ?????? ?????? */}
        {nameIntegrity != "" && (
          <ErrorMesageBox>{nameIntegrity}</ErrorMesageBox>
        )}
        <ProfileInputTitle>?????? ???????????? </ProfileInputTitle>
        <ProfileInput
          fullWidth
          id="account_old_password"
          name="account_old_password"
          onChange={onChange}
          value={accountOldPassword}
          type="password"
          // variant="standard"
        />{" "}
        <br />
        {/* ????????? ?????? ?????? */}
        {oldPasswordIntegrity != "" && (
          <ErrorMesageBox>{oldPasswordIntegrity}</ErrorMesageBox>
        )}
        <ProfileInputTitle>??? ???????????? </ProfileInputTitle>
        <ProfileInput
          fullWidth
          id="account_new_password"
          name="account_new_password"
          onChange={onChange}
          value={accountNewPassword}
          type="password"
          // variant="standard"
          // type="password"
        />{" "}
        <br />
        {/* ????????? ?????? ?????? */}
        {newPasswordIntegrity != "" && (
          <ErrorMesageBox>{newPasswordIntegrity}</ErrorMesageBox>
        )}
        <ProfileInputTitle>???????????? ?????? </ProfileInputTitle>
        <ProfileInput
          fullWidth
          id="account_new_password_confirm"
          name="account_new_password_confirm"
          onChange={onChange}
          value={accountNewPasswordConfirm}
          type="password"
          // variant="standard"
        />
        <br />
        {/* ????????? ?????? ?????? */}
        {newPasswordConfirmIntegrity != "" && (
          <ErrorMesageBox>{newPasswordConfirmIntegrity}</ErrorMesageBox>
        )}
        <ProfileButton onClick={onSubmit}>????????????</ProfileButton>
      </ProfileForm>
    </ProfileTemplate>
  );
}
