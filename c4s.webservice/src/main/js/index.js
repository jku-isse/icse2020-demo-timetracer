import React from 'react'
import { render } from 'react-dom'
import { BrowserRouter } from 'react-router-dom'
import Menu from './Menu';

render((
  <BrowserRouter>
    <Menu />
  </BrowserRouter>
), document.getElementById('react'));