'use strict';

// tag::vars[]
const React = require('react');
import { useState } from "react";
import ReactDataGrid from "react-data-grid";
import { Toolbar, Data } from "react-data-grid-addons";
import { Button, ButtonToolbar, Modal, Card, Alert, Badge, Container, Row, Col, CardDeck, DropdownButton, Dropdown} from "react-bootstrap";
import Collapsible from 'react-collapsible';
import { Link } from 'react-router-dom'
const selectors = Data.Selectors;
let moment = require('moment');
const {
	DraggableHeader : { DraggableContainer }
} = require("react-data-grid-addons");
//icons
import {TiTickOutline} from 'react-icons/ti';
import {TiTimesOutline} from 'react-icons/ti';
import {TiWarningOutline} from 'react-icons/ti';
import {MdHelpOutline} from 'react-icons/md';
import {MdCallMissedOutgoing} from 'react-icons/md';
// end::vars[]

// tag::app[]
export default class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
				modalShow: false,
                modalInstance: {},
                modalWfiID: '',
                wfi: [],
                workflow: [],
                whatToShow: props.match.params.wfiid
		};
		this.handler = this.handler.bind(this);
	}

	componentDidMount() {
        if (this.state.whatToShow == 'all'){
            fetch('/wfi').then(result => {
                return result.json();
            }).then(data => {
                this.setState({wfi: data});
            })
         } else { //whatToShow == 'WF'
             fetch(`/wfi/WF?wfiId=${this.state.whatToShow}`).then(result => {
                return result.json();
            }).then(data => {
                this.setState({wfi: data});
            })
         }
        fetch('/workflow').then(result => {
            return result.json();
        }).then(data => {
            this.setState({workflow: data});
        })
    }
	
	handler(task, wfiid) {
	    this.setState({
	    	modalShow: true,
            modalInstance: task,
            modalWfiID: wfiid
	    })
	    
	  }

	render() {
		let modalClose = () => this.setState({ modalShow: false });
		
		return (
			<div>
                <Button href='/' variant='outline-primary'>Back</Button>
		        <QAModal
		          show={this.state.modalShow}
		          onHide={modalClose}
                  task={this.state.modalInstance}
		          wfiid={this.state.modalWfiID}
		        />
				<MyTable 
					handler = {this.handler}
					wfi={this.state.wfi}
					workflow={this.state.workflow}
				/>
			</div>
		)
	}
}
// end::app[]

// tag::mytable[]
class MyTable extends React.Component{
	render() {
		let data = {
				wfi: this.props.wfi,
				workflow: this.props.workflow
		}
		if (isReady(data)){// wait until data is loaded
			
			//fill columns
			const columns = [];
			columns.push({key: 'wfi', name: 'Workflow Instance', width: 300, sortable: true, filterable: true, resizable: true});
            this.props.workflow.map(d => {
            	if (d.tasks > 0) {
            		columns.push({key: d.id, name: d.id, sortable: false, resizable: true, draggable: true})
            	}
            });
			//fill rows
			let key = 0;
            let rows = [];
            this.props.wfi.map(wfi => {
                let row = {};
                row['wfi'] = <WorkflowInstanceCell key={wfi.id} id={wfi.id} wfProps={wfi.wfProps}/>
                wfi.tasks.map(t => {
                    row[t.definition.id] = <MyCard key={key++} handler={this.props.handler} instance={t} wfiid={wfi.id}/>
                });
                rows.push(row);
            });

			//return a ReactDataGrid
			return (<Grid rows={rows} columns={columns} />)
			
		} else {
			return ( <div>loading...</div> )
		}
	}
}
// end::mytable[]

class WorkflowInstanceCell extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
            id: this.props.id,
            wfProps: this.props.wfProps
		};
	}
	
	render () {
		let properties = [];
		for (let p in this.state.wfProps) {
			if (p === 'Issue Type'){
				properties.push(<div key={p}>{p+': '}<Badge variant="primary">{this.state.wfProps[p]}</Badge></div>);
			} else {
		    	properties.push(<div key={p}>{p+': '+this.state.wfProps[p]}</div>);
			}
		}
		return (
			<div>
				<div><b>{this.state.id}</b></div>
				{properties}
			</div>
		)
	}
}

class MyCard extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
            task: this.props.instance,
            wfiid: this.props.wfiid
		};
	}
	
	render() {
		
		let dis = false;
		let borderStyle = 'success';
		let variant = 'outline-primary'
		let link = 'No resource so far..';
        //set resource link
        if (this.state.task.resource != null){
			link = <a key={this.state.task.resource.href} target="_blank" href={this.state.task.resource.href}>{this.state.task.resource.title}</a>
		} else {
			borderStyle = 'warning';
		}
		//set card appearence
        if (this.state.task.checkDoc == null) {
        	return (<div></div>)
        }
		if (this.state.task.checkDoc.constraints.length === 0){
			dis = true;
			variant = 'outline-secondary';
		} else {
			this.state.task.checkDoc.constraints.map(c => {
				if (c.unsatisfied.length > 0){
					borderStyle = 'danger';
				}
			});
		}
		return (
				<Card bg='light' border={borderStyle}>
			    <Card.Header>{this.state.task.resource.context}</Card.Header>
			    <Card.Body>
			      <Card.Text>
			       Jira Link: {link}
			      </Card.Text>
			      <ButtonToolbar>
			        <Button
				        variant={variant}
				        onClick={() => this.props.handler(this.state.task, this.state.wfiid)}
                        disabled={dis}
                        block
			        >
			          Quality Checks
			        </Button>
                    
		        </ButtonToolbar>
			    </Card.Body>
			  </Card>
		)
	}
}

function Grid(props) {
	  const [columns, setColumns] = useState(props.columns);
	  const [rows, setRows] = useState(props.rows);
	  const [filters, setFilters] = useState({});
	  const filteredRows = getRows(rows, filters);
	  
	  const onHeaderDrop = (source, target) => {
		   const columnCopy = Array.from(columns);
		   const columnSourceIndex = columns.findIndex(
		     i => i.key === source
		   );
		   const columnTargetIndex = columns.findIndex(
		     i => i.key === target
		   );
		   columnCopy.splice(
		     columnTargetIndex,
		     0,
		     columnCopy.splice(columnSourceIndex, 1)[0]
		   );
		   const emptyColumns = Array.from([]);
		   setColumns(emptyColumns);
		   const reorderedColumns = Array.from(columnCopy);
		   setColumns(reorderedColumns);
	  };
	  
	  return (
		<DraggableContainer onHeaderDrop={onHeaderDrop}>
		    <ReactDataGrid
		      columns={columns}
		      rowGetter={i => filteredRows[i]}
		      rowsCount={filteredRows.length}
		      headerRowHeight={40}
		      rowHeight={190}
		      minHeight={2000}
		      onGridSort={(sortColumn, sortDirection) =>
		      	setRows(sortRows(rows, sortColumn, sortDirection))//why initial rows?
		      }
		      toolbar={<Toolbar enableFilter={true} />}
		      onAddFilter={filter => setFilters(handleFilterChange(filter))}
		      onClearFilters={() => setFilters({})}
		    />
	    </DraggableContainer>
	  );
}
	    
class QAModal extends React.Component {
	render() {
        if (this.props.show){
            let body = [];
            let i = 0;
            
            //IDs for REST communication with backend
            let wfiid = this.props.wfiid;
            let wftID = this.props.task.id.replace('#', '%23');//hashtag not allowed in URL

            this.props.task.checkDoc.constraints.sort(function(a, b) { return a.orderInParentDoc - b.orderInParentDoc;}).map(c => {
                let plinks = [];
                c.fulfilled.map(p => {
                    plinks.push(
                        <span key={i++}>
                            {p.context}
                            <span>: </span>
                            <a key={p.href} target="_blank" href={p.href}>{p.title}</a>
                            <br/>
                        </span>
                    );
                });
                let nlinks = [];
                c.unsatisfied.map(n => {
                    nlinks.push(
                        <span key={i++}>
                            {n.context}
                            <span>: </span>
                            <a key={n.href} target="_blank" href={n.href}>{n.title}</a>
                            <br/>
                        </span>
                    );
                });
                let evaluationMessage;
                let alertStyle = 'dark'; //should never stay dark
                if (c.evaluationStatus === 'SUCCESS'){
                    alertStyle = 'danger';
                    if (plinks.length > 0 && nlinks.length === 0) alertStyle = 'success';
                    if (plinks.length > 0 && nlinks.length > 0) alertStyle = 'warning';
                } else if (c.evaluationStatus === 'FAILURE'){
                    evaluationMessage = <Badge variant="danger">EvalMsg: {c.evaluationStatusMessage.substring(0, 78)}</Badge>
                } else if (c.evaluationStatus === 'NOT_YET_EVALUATED'){
                    alertStyle = 'secondary';
                }
                body.push(
                        <Container key={i++}>
                        <Row>
                            <Col>
                                <Collapsible trigger={
                                	<Alert variant={alertStyle}>
                                		<Container key={i++}>
	                                		<Row>
		                                    	<Col md="auto">
		                                    		{alertStyle === 'secondary' &&
		                                    			<MdHelpOutline size={45}/>
		                                    		}
		                                    		{alertStyle === 'danger' &&
		                                    			<TiTimesOutline size={45} color="red"/>
		                                    		}
		                                    		{alertStyle === 'success' &&
		                                    			<TiTickOutline size={45} color="green"/>
		                                    		}
		                                    		{alertStyle === 'warning' &&
		                                    			<TiWarningOutline size={45} color="orange"/>
		                                    		}
		                                    		{alertStyle === 'dark' &&
		                                    			<MdCallMissedOutgoing size={45}/>
		                                    		}
		                                    	</Col>
		                                        <Col>
			                                        <Alert.Link href='#'>
			                                            {c.description}
			                                        </Alert.Link>
			                                        <br/>
			                                        <Badge variant="primary">evaluated</Badge>
			                                        <span> </span>
			                                        {moment(c.lastEvaluated).fromNow()}
			                                        <span> </span>
			                                        <Badge variant="primary">changed</Badge>
			                                        <span> </span>
			                                        {moment(c.lastChanged).fromNow()}                                    
			                                        <br/>
			                                        {evaluationMessage}
		                                        </Col>
	    		                            </Row>
    		                            </Container>
                                    </Alert>
		                         
                                }>
                                    <CardDeck>
                                    {nlinks.length > 0 &&
                                        <Card border="danger">
                                            <Card.Body>
                                            <Card.Title>Failed constraints</Card.Title>
                                            <Card.Text>
                                                {nlinks}
                                            </Card.Text>
                                            </Card.Body>
                                        </Card>
                                    }
                                    {plinks.length > 0 &&
                                        <Card border="success">
                                            <Card.Body>
                                            <Card.Title>Fulfilled constraints</Card.Title>
                                            <Card.Text>
                                                {plinks}
                                            </Card.Text>
                                            </Card.Body>
                                        </Card>
                                    }
                                    </CardDeck>
                                    <br/>
                                </Collapsible>
                            </Col>
                            <Col lg='auto' md='auto' sm='auto' xl='auto' xs='auto'>
                                    <Button 
                                    	onClick={() => {
                                    		sendMsg(`../checks/?constrId=${c.id}&constrType=${c.constraintType}&wfiId=${wfiid}&wftId=${wftID}`);
                                    	}}
                                    	variant="outline-primary"
                                    >Check</Button>
                            </Col>
                        </Row>
                        </Container>
                    )
            });
            return (
            <Modal
                {...this.props}
                size='lg'
                // dialogClassName='w-auto p-3'
                centered
            >
                <Modal.Header closeButton>
                <Modal.Title id="contained-modal-title-vcenter">
                    {this.props.task.id}
                </Modal.Title>
                </Modal.Header>
                <Modal.Body>
        
                {body}
                
                </Modal.Body>
                <Modal.Footer>
                <Button variant="primary" onClick={() => {
                    sendMsg(`../checks/?constrId=*&constrType=*&wfiId=${wfiid}&wftId=${wftID}`);
                }}>Check all</Button>
                <Button variant="primary" onClick={this.props.onHide}>Close</Button>
                </Modal.Footer>
            </Modal>
            );
        } else {return null;}
    }
}
function sendMsg(url){
	fetch(url)
    .then(response => response.json())
    .then(data => {
    	if(!alert(data.msg + '\n\n' + data.payload)){window.location.reload();}
    })
    .catch(error => console.error(error));
}
const sortRows = (initialRows, sortColumn, sortDirection) => rows => {
	  const comparer = (a, b) => {
	    if (sortDirection === "ASC") {
	      return a[sortColumn].key > b[sortColumn].key ? 1 : -1;
	    } else if (sortDirection === "DESC") {
	      return a[sortColumn].key < b[sortColumn].key ? 1 : -1;
	    }
	  };
	  return sortDirection === "NONE" ? initialRows : [...rows].sort(comparer);
};
	
const handleFilterChange = filter => filters => {
	  const newFilters = { ...filters };
	  if (filter.filterTerm) {
	    newFilters[filter.column.key] = filter;
	  } else {
	    delete newFilters[filter.column.key];
	  }
	  return newFilters;
};

function getRows(rows, filters) {
	  const r = selectors.getRows({ rows, filters });
	  return r;
}

function isReady(data){
	let rdy = true;
	for (let d in data){
		if (data[d].length === 0)
			rdy = false;
	}
	return rdy;
}