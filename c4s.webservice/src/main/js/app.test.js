'use strict';

import React from 'react';
import ReactDOM from 'react-dom';
import renderer from 'react-test-renderer';
import App from './app';
import MyCard from './app';
import MyTable from './app';
import MyModal from './app';

jest.mock('./client', () => () => ({
	  client: () => 10,
	  done: () => 20
}));


it('App renders without crashing', () => {
  const div = document.createElement('div');
  ReactDOM.render(<App />, div);
});

it('MyCard renders without crashing', () => {
	  const div = document.createElement('div');
	  ReactDOM.render(<MyCard instance={'a'} />, div);
});

it('MyTable renders without crashing', () => {
	  const div = document.createElement('div');
	  ReactDOM.render(<MyTable />, div);
});

it('MyModal renders without crashing', () => {
	  const div = document.createElement('div');
	  ReactDOM.render(<MyModal />, div);
});