import React, { Component, useRef } from "react";
import styled from "styled-components";
import PWDayBox from "../PRDayBox";
import PRPlaceBox from "../PRPlaceBox";
import PWPlaceBox from "../PRPlaceBox";

class PRPlaceBoxGenerator {
  constructor(places, setPlaces) {
    this._array = new Array();
    this._stateArray = new Array();
    this._index = -1;

    this.places = places;
    this.setPlaces = setPlaces;
  }

  //단일 아이템 카드 삽입
  addBox(box) {
    this._index++;
    this._array.push(
      <PRPlaceBox
        key={this._index}
        did={box.did}
        pid={this._index}
        data={box.data}
        gen={this}
        line={box.line}
      />
    );

    this.setPlaces(this.render());
  }

  //아이템 카드 배열 삽입
  addBoxArray(array) {
    array.forEach((box) => {
      this._index++;
      this._array.push(
        <PRPlaceBox
          key={this._index}
          did={box.did}
          pid={this._index}
          data={box.data}
          gen={this}
          line={box.line}
        />
      );
    });

    this.setPlaces(this.render());
  }

  removeTop() {
    this._array.pop();
    this._index--;

    this.setPlaces(this.render());
  }

  remove(key) {
    this._array[key] = <></>;
    this._stateArray = this._stateArray
      .slice(0, key)
      .concat(this._stateArray.slice(key + 1, this._stateArray.length));

    var tempArray = new Array();

    for (var i = 0; i < this._array.length; i++) {}
    // for (var i = key; i < this._array.length; i++) {
    //   this._array[i] = this._array[i + 1];
    // }
    // this._array.pop();

    this.setPlaces(this.render());
  }

  //아이템 리스트 초기화
  clear() {
    this._array = null;
    this._array = new Array();
    this._stateArray = null;
    this._stateArray = new Array();
  }

  //폼데이터 추출
  getData() {
    var formData = new FormData();
    var data = { data: [] };
    for (var i = 0; i < this._stateArray.length; i++) {
      const e = this._stateArray[i];
      data = { data: [...data.data, e] };
    }

    return data;
  }

  //렌더링
  render() {
    return <div className="pw_place_box_list">{this._array}</div>;
  }
}

export default PRPlaceBoxGenerator;
