package com.sifiso.codetribe.minisasslibrary.interfaces;

import com.sifiso.codetribe.minisasslibrary.dto.TeamMemberDTO;

import java.util.List;

public interface TeamMemberAddedListener {

	public void onTeamMemberAdded(List<TeamMemberDTO> list);
}
