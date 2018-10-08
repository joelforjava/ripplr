<div class="col-12 col-md-10 col-lg-8">
    <g:form action="results" class="card card-sm">
        <div class="card-body row no-gutters align-items-center">
            <div class="col-auto">
                <i class="glyphicon glyphicon-search h4 text-body"></i>
            </div>
            <div class="col">
                <g:textField class="form-control form-control-lg form-control-borderless" id="searchBox" name="q" placeholder="${message(code:'search.page.input.placeholder.label', default: 'Search Ripples')}" />
            </div>
            <div class="col-auto">
                <g:submitButton class="btn btn-primary btn-lg" name="search" value="${message(code:'search.page.button.label', default: 'Search')}" />
            </div>
        </div>
    </g:form>
</div>
