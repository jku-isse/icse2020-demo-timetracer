import React from 'react'
import { useState } from "react";
import ReactDOM from "react-dom";
import ReactDataGrid from "react-data-grid";
import { Toolbar, Data } from "react-data-grid-addons";
const selectors = Data.Selectors;
import { Link } from 'react-router-dom'
import { Button, ButtonToolbar, ButtonGroup, Form, Container, Row, Col} from "react-bootstrap";

export default class List extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
            wfis: []
		};
		this.getData = this.getData.bind(this);
	}

	getData(){
		fetch('/wfis').then(result => {
            return result.json();
        }).then(data => {
            this.setState({wfis: data});
        })
	}
	
	componentDidMount() {
        this.getData();
    }
	

	render() {
		let columns = [];
		columns.push({key: 'wfi', name: 'Workflow Instance ID', filterable:true, resizable: true});
		columns.push({key: 'link', name: 'Link', width:60, resizable: true});
		let rows = [];
		let key = 0;
		this.state.wfis.map(p => {	//TODO map may throws an exception if only one workflow exists
			let row = {};
            row['wfi'] = p.id;
            row['link'] = <Link key={key++} to={`/${p.id}`}>view</Link>;
            rows.push(row);
		});
		
        return(

            <div className='container'>
                <br/><h2>Add items to monitor</h2>
                <FilterForm/>
                <br/><h2>Pick workflow instance to show</h2>
                <Grid columns={columns} initialRows={rows} />
            </div>
        )
	}
}

class FilterForm extends React.Component{
	
	constructor(props) { 
		super(props);
		this.state = {
				value: '',
				value2: ''
		};
		this.handleChange = this.handleChange.bind(this);
		this.handleChange2 = this.handleChange2.bind(this);
	}  
	handleChange (event) {
	    this.setState({ value: event.target.value });
	  }
	handleChange2 (event) {
	    this.setState({ value2: event.target.value });
	  }
	render() {
	return(
		<Container>
		  <Row>
		    <Col>
			    <Form>
				  <Form.Group>
				    <Form.Label>Add via JAMA filter</Form.Label>
				    <Form.Control value={this.state.value} onChange={this.handleChange} type="text" placeholder="Enter filter ID" />
				    <Form.Text className="text-muted">
				       e.g. 12345
				    </Form.Text>
				  </Form.Group>
				  <Button variant="outline-primary" type="button" onClick={() => {
					    this.setState({value: ''});
	                    this.setState({value2: ''});
	                    sendMsg(`../add/?filterId=${this.state.value}`);
	                }}>
				      Add via Filter
				  </Button>
				</Form>
		    </Col>
		    <Col>
			    <Form>
				  <Form.Group>
			        <Form.Label>Add JAMA WP/SubWP item</Form.Label>
				    <Form.Control value={this.state.value2} onChange={this.handleChange2} type="text" placeholder="Enter item ID" />
				    <Form.Text className="text-muted">
				       e.g. 1234567
				    </Form.Text>
				  </Form.Group>
				  <Button variant="outline-primary" type="button" onClick={() => {
					    this.setState({value: ''});
	                    this.setState({value2: ''});
					    sendMsg(`../add/?featureId=${this.state.value2}`);
	                }}>
				    Add via Item
				  </Button>
				</Form>
		    </Col>
		  </Row>
		</Container>
	);
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

function Grid({ initialRows, columns }) {
	  const [rows, setRows] = useState(initialRows);
	  const [filters, setFilters] = useState({});
	  const [selectedIndexes, setSelectedIndexes] = useState([]);
	  //const filteredRows = getRows(rows, filters);
	  const filteredRows = getRows(initialRows, filters);
	  let onRowsSelected = rows => {
		  setSelectedIndexes(selectedIndexes.concat(rows.map(r => r.rowIdx)));
	  };
	  let onRowsDeselected = rows => {
		    let rowIndexes = rows.map(r => r.rowIdx);
		    setSelectedIndexes(selectedIndexes.filter(i => rowIndexes.indexOf(i) === -1));
	  };
	  const rowText = selectedIndexes.length === 1 ? "row" : "rows";
	  return (
		  <div>
	        <span>
	          {selectedIndexes.length} {rowText} selected
	        </span>
		    <ReactDataGrid
		      columns={columns}
		      rowGetter={i => filteredRows[i]}
		      rowsCount={filteredRows.length}
		      onGridSort={(sortColumn, sortDirection) =>
		      	setRows(sortRows(initialRows, sortColumn, sortDirection))
		      }
		      toolbar={<Toolbar enableFilter={true} />}
		      onAddFilter={filter => setFilters(handleFilterChange(filter))}
		      onClearFilters={() => setFilters({})}
		      rowSelection={{
		            showCheckbox: true,
		            enableShiftSelect: true,
		            onRowsSelected: onRowsSelected,
		            onRowsDeselected: onRowsDeselected,
		            selectBy: {
		              indexes: selectedIndexes
		            }
		          }}
		     />
		     <br/>
		     <ButtonToolbar>
			   <ButtonGroup>
			   	 <Button variant='outline-primary' href='/#/all'>View all items</Button>
			     <Button variant='outline-primary' href={`/#/${selectedIndexes.map(i => initialRows[i].wfi).join(',')}`}>View selected items</Button>
			     <Button variant='outline-danger' onClick={() => sendMsg(`../delete/?wfiIds=${selectedIndexes.map(i => initialRows[i].wfi).join(',')}`)}>Delete selected items</Button>
			   </ButtonGroup>
		     </ButtonToolbar>
		   </div>
	  );
}
    const sortRows = (initialRows, sortColumn, sortDirection) => rows => {
  	  const comparer = (a, b) => {
  	    if (sortDirection === "ASC") {
  	      return a[sortColumn] > b[sortColumn] ? 1 : -1;
  	    } else if (sortDirection === "DESC") {
  	      return a[sortColumn] < b[sortColumn] ? 1 : -1;
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
  	  return selectors.getRows({ rows, filters });
  }
