import React from 'react';

import { textInput } from '../../../css/field.css';
import LabeledField from '../LabeledField';

export default class TextInput extends LabeledField {
	constructor(props) {
		super(props);
	}

	render() {
		let placeholderText = null;
		if (this.props.isSet) {
			placeholderText = '********';
		}
		let inputClass = this.props.inputClass;
		if (!inputClass) {
			inputClass = 'form-control';
		}
		let inputDiv = null;
		if (this.props.readOnly) {
			inputDiv = <div className="col-sm-8"><input type="text" readOnly className={inputClass} name={this.props.name} value={this.props.value} placeholder={placeholderText} /></div>;
		} else {
			inputDiv = <div className="col-sm-8"><input type="text" className={inputClass} name={this.props.name} value={this.props.value} onChange={this.props.onChange} placeholder={placeholderText} /></div>;
		}
		return (
				super.render(inputDiv)
		)
	}
}
