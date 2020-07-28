import React from 'react'
import { HashRouter , Route } from 'react-router-dom'
import List from './List'
import App from './app'

const Menu = () => (
    <HashRouter >
        <Route exact path='/' component={List}/>
        <Route path='/:wfiid' component={App}/>
    </HashRouter >
)


export default Menu