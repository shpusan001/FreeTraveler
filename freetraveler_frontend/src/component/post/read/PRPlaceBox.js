import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import styled from "styled-components";
import { changeField, savePostIntegerity } from "../../../module/posting";
import placelogo from "../../../resource/img/placelogo2.png";
import Button from "../buttons/DayButton";
import { FormControl, TextField, NativeSelect } from "@mui/material";
import palette from "../../../lib/styles/palette";
import { IoOpenOutline } from "react-icons/io5";

const PRPlaceBoxTemplate = styled.div`
  /* width: auto; */
  /* margin: 15px; */
  margin-top: 15px;
  margin-bottom: 30px;
  background-color: white;

  box-shadow: 0 0 5px ${palette.gray[12]};

  padding: 1.1rem;
  border-width: 0px;
  border-style: solid;
  border-radius: 0.4rem;
  overflow-x: hidden;
  overflow-y: hidden;
  display: flex;
  flex-direction: column;

  .logo {
    width: 100px;
    height: auto;
    margin-left: -13px;
    margin-bottom: 10px;
  }

  .button {
    width: 60%;
    margin-top: 15px;
    padding-left: 20%;
    padding-right: 20%;
  }

  @media screen and (max-width: 612px) {
    .button {
      width: 70%;
      margin-top: 15px;
      padding-left: 15%;
      padding-right: 15%;
    }
  }
  @media screen and (min-width: 1012px) {
    .button {
      width: 30%;
      margin-top: 15px;
      padding-left: 35%;
      padding-right: 35%;
    }
  }
`;

const PRPlaceBoxTitle = styled.div`
  margin-top: 1rem;
  /* margin-left: 1rem; */

  font-weight: 400;
  /* line-height: 1.9rem; */

  font-size: 1.2rem;
  color: rgb(26, 26, 26);
  text-align: left;
`;

const PostInput = styled.div`
  margin: 5px;
  font-size: 1.1rem;
  font-weight: 400;
  color: rgb(26, 26, 26);
  text-align: left;
  .title-text {
    margin-left: -5px;
    margin-top: 10px;
    margin-bottom: 20px;
    text-align: center;
    width: 55px;
    margin-bottom: 5px;
    margin-right: 5px;
    font-weight: 700;
    font-size: 13px;
    line-height: 24px;
    border-radius: 4px;
    color: white;
    background-color: ${palette.mint[1]};
  }
  .title-data-text {
    margin-top: 20px;
  }
`;

const PlaceRemoveBtn = styled.div`
  width: 100%;
  text-align: center;
`;

const PRPlaceBoxText = styled.div``;

const PRPlaceBoxImage = styled.div`
  .image-box {
    width: 100%;
    height: 300px;
    overflow: hidden;
    margin: 0 auto;
  }

  .image-inbox {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
`;
const CommentBox = styled.div`
  /* margin-top: 1rem;
  display: inline-flex;
  outline: none;
  border-width: 1.5px;
  border-style: solid;
  border-radius: 10px;
  color: white;
  font-weight: bold;
  width: 100%;
  border-color: ${palette.mint[0]}; */

  /* display: flex;
  background-color: rgb(248, 248, 248);
  padding: 20px;

  border-end-end-radius: 10px;
  border-end-start-radius: 10px; */
  .hr-color {
    order: solid 1px ${palette.gray[16]};
  }
`;

const trimDistance = function (distance) {
  let trim = Math.floor(distance);
  let output = "";

  if (trim >= 1000) {
    trim = trim / 1000;
    output = trim + "km";
  } else {
    output = trim + "m";
  }

  return output;
};

const getTimeToArrival = function (trans, distance) {
  let output = 0;
  if (trans == "walk") {
    output = distance / 1.1; //?????? ?????? 1.1m
  } else if (trans == "public") {
    output = distance / 6; //???????????? ?????? 6m
  } else if (trans == "car") {
    output = distance / 12; // ????????? ?????? 12m
  }
  //????????? ??????
  output = Math.floor(output);
  if (output < 60) {
    output = output.toFixed(2);
    output = output + "???";
  } else if (output >= 60 && output < 3600) {
    output = output / 60;
    output = output.toFixed(2);
    output = output + "???";
  } else {
    output = output / 3600;
    output = output.toFixed(2);
    output = output + "??????";
  }

  return output;
};

export default function PRPlaceBox({ did, pid, data = {}, line }) {
  // ???????????? ??????
  // let data = {
  //   name: "",
  //   loc: "",
  //   cost: "",
  //   img: "",
  //   content: "",
  //   trans: "",
  // };

  return (
    <PRPlaceBoxTemplate>
      <img className="logo" src={placelogo} />
      {/* ????????? ?????? */}
      <PRPlaceBoxImage>
        <div className="image-box">
          <img className="image-inbox" src={data.img /* ????????? ?????? */}></img>
        </div>
      </PRPlaceBoxImage>

      {/* ?????? ?????? */}
      <PRPlaceBoxTitle>
        <b>?????? | </b>
        {data.placeName}
      </PRPlaceBoxTitle>
      {/* ?????? ?????? */}
      <PRPlaceBoxTitle>
        <b>?????? | </b>
        {data.loc}
      </PRPlaceBoxTitle>

      {/* ?????? ?????? */}
      <PRPlaceBoxTitle>
        <b>?????? | </b>
        {data.cost}
      </PRPlaceBoxTitle>

      {/* ?????? ?????? ?????? */}
      <PRPlaceBoxTitle>
        <b>???????????? | </b>
        {data.trans}
      </PRPlaceBoxTitle>

      {/* ?????? ?????? */}
      <PRPlaceBoxTitle>
        <b>?????? | </b>
        {line == null ? "?????????" : trimDistance(line.getLength())}
      </PRPlaceBoxTitle>

      {/* ?????? ?????? */}
      <PRPlaceBoxTitle>
        <b>???????????? | </b>
        {line == null
          ? "?????????"
          : getTimeToArrival(data.trans, line.getLength())}
      </PRPlaceBoxTitle>
      {/* ?????? ?????? */}
      <CommentBox>
        <br />
        <hr />
        <br />
        {/* <hr className="hr-color" /> */}
        <PostInput>
          {/* <div class="title-text"> STORY </div> */}
          {data.content}
        </PostInput>
      </CommentBox>
    </PRPlaceBoxTemplate>
  );
}
