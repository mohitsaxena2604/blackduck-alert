import React, { Component } from 'react';

import {checkboxInput, fieldError} from '../../../css/field.css';

export default class CheckboxInput extends Component {
	constructor(props) {
		super(props);
	}

    render() {
        let errorDiv = null;
        if (this.props.errorName && this.props.errorValue) {
            errorDiv = <p className={fieldError} name={this.props.errorName}>{this.props.errorValue}</p>;
        }

        let inputClass = this.props.inputClass;
        if (!inputClass) {
            inputClass = checkboxInput;
        }

        let isChecked = false;
        if (this.props.value) {
            if (this.props.value == "true") {
                isChecked = true;
            } else if (this.props.value === true) {
                isChecked = true;
            }
        }

        let inputDiv = null;
        if (this.props.readOnly) {
            inputDiv = <div className="col-sm-8"><input type="checkbox" readOnly disabled="disabled" className={inputClass} name={this.props.name} checked={isChecked}/></div>;
        } else {
            inputDiv = <div className="col-sm-8"><input type="checkbox" className={inputClass} name={this.props.name} checked={isChecked} onChange={this.props.onChange} /></div>;
        }

        return (
            <div className="form-group">
				<div className="checkbox">
					<label className="col-sm-4 control-label">{this.props.label}</label>
					{inputDiv}
					{errorDiv}
				</div>
            </div>
        )
    }
}
