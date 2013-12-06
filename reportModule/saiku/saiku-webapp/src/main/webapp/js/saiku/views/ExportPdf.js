/*
 * SaveQuery.js
 * 
 * Copyright (c) 2011, OSBI Ltd. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
/**
 * The export pdf query dialog
 */
var ExportPdf = Modal.extend({
    type: "exportpdf",
    closeText: "ExportPdf",
    
    buttons: [
        { text: "Export", method: "exportPdf" }
    ],
    
    initialize: function(args) {
        // Append events
        var name = args.query.name ? args.query.name : "";
        this.query = args.query;
        this.message =  _.template("<form id='export_pdf_form'>" +
            "<label for='name'>Report Title " + 
            "<input type='text' name='name' value='' />" +
            "<br><br><label name='warn'>" +
            "</form>")({});
        _.extend(this.options, {
            title: "Export Pdf"
        });
        
        // Focus on query name
        $(this.el).find('input').select().focus();
        _.bindAll(this, "close");
    },
    
    exportPdf: function(event) {
        var reportTitle = $(this.el).find('input[name="name"]').val();
        if(reportTitle == ""){
			$(this.el).find('label[name="warn"]').text("*Title can't be Empty");
			$(this.el).find('label[name="warn"]').css('color','red');
			return false;
		}
		 window.location = Settings.REST_URL +
            Saiku.session.username + "/query/" + 
            this.query.id + "/export/pdf?reportTitle="+reportTitle;
            this.close();
        return false;
    },
}); 


